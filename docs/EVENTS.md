# Event & Kafka Design

## Amaç

Kafka'nın temel kullanım amacı PostgreSQL business state ile Elasticsearch read model'ini loosely coupled biçimde senkronize etmektir.

## Domain/Integration Events

```text
JobCreated
JobUpdated
JobPublished
JobClosed
JobDeleted
ApplicationSubmitted
ApplicationStatusChanged
CandidateProfileUpdated
```

## Topic Önerileri

Başlangıçta event type başına topic açmak yerine bounded-context bazlı topic tercih edilebilir:

```text
job.events.v1
application.events.v1
candidate.events.v1
```

## Event Envelope

```json
{
  "eventId": "uuid",
  "eventType": "JobPublished",
  "eventVersion": 1,
  "occurredAt": "2026-08-30T18:00:00Z",
  "aggregateId": "job-id",
  "correlationId": "request-id",
  "payload": {}
}
```

## Transactional Outbox

Problem:

```text
DB commit başarılı
Kafka publish başarısız
```

çift-write inconsistency yaratır.

Çözüm:

Aynı PostgreSQL transaction'ında:

```text
jobs
outbox_events
```

tabloları yazılır.

Ayrı publisher outbox kayıtlarını Kafka'ya gönderir.

## Consumer

Job search indexer:

```text
Kafka Job Event
      ↓
Deserialize
      ↓
Load latest Job projection if needed
      ↓
Upsert Elasticsearch document
```

## Idempotency

Consumer aynı event'i birden fazla kez alabilir.

Index operation idempotent olmalıdır.

Elasticsearch document ID olarak `jobId` kullanılması upsert işlemini kolaylaştırır.

## Retry & DLT

Retry policy örneği:

```text
1s
5s
30s
2m
```

Belirli retry sonrasında:

```text
job.events.v1.DLT
```

DLT kayıtları observability/alerting ile takip edilmelidir.
