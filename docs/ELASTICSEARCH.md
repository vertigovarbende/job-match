# Elasticsearch Design

## 1. Elasticsearch'in Rolü

Elasticsearch yalnızca arama ve discovery read model'i olarak kullanılır.

**Source of truth PostgreSQL'dir.**

Elasticsearch unavailable olduğunda:

- yeni ilan PostgreSQL'e yazılabilir;
- Kafka event'i daha sonra tekrar tüketilebilir;
- indeks eventual consistency ile toparlanabilir.

## 2. Index

İlk index alias yaklaşımı:

```text
job-search-read -> job-search-v1
job-search-write -> job-search-v1
```

Mapping değişikliklerinde:

```text
job-search-v1
       ↓ reindex
job-search-v2
       ↓ alias switch
```

Zero/minimum downtime migration yapılabilir.

## 3. Suggested Document

```json
{
  "jobId": "uuid",
  "title": "Java Backend Developer",
  "description": "...",
  "company": {
    "id": "uuid",
    "name": "Example Tech",
    "industry": "FinTech"
  },
  "requiredSkills": ["java", "spring boot", "postgresql"],
  "preferredSkills": ["kafka", "redis"],
  "seniority": "MID",
  "employmentType": "FULL_TIME",
  "workplaceType": "HYBRID",
  "location": {
    "city": "Istanbul",
    "country": "TR",
    "point": {
      "lat": 41.0082,
      "lon": 28.9784
    }
  },
  "salaryMin": 60000,
  "salaryMax": 90000,
  "currency": "TRY",
  "publishedAt": "2026-08-30T15:00:00Z"
}
```

## 4. Mapping Strategy

- `title`: text + keyword
- `description`: text
- `company.name`: text + keyword
- `skills`: keyword; gerekirse text sub-field
- enums: keyword
- salary: integer/long
- publishedAt: date
- geo location: geo_point

## 5. Relevance

Örnek weighting:

```text
title^5
requiredSkills^4
preferredSkills^2
company.name^2
description^1
```

Örnek query mantığı:

```text
bool
├── must
│   └── multi_match(q)
├── filter
│   ├── workplaceType
│   ├── seniority
│   ├── salary range
│   └── location
└── should
    └── freshness boost
```

## 6. Features

### Highlighting

Arama sonucu içinde eşleşen kelimeler vurgulanabilir.

### Autocomplete

Seçenekler:

- completion suggester
- edge_ngram analyzer
- search_as_you_type

İlk sürümde `search_as_you_type` veya edge n-gram yeterlidir.

### Typo Tolerance

`fuzziness: AUTO` dikkatli kullanılmalıdır; kısa kelimelerde ve yüksek trafikte maliyetli olabilir.

### Synonyms

Örneğin:

```text
js, javascript
postgres, postgresql
spring, spring boot
k8s, kubernetes
```

### Geo Search

Kullanıcı lokasyonuna X km mesafedeki ilanlar için `geo_distance` kullanılabilir.

## 7. Pagination

Basit ekranlar için `from/size` kullanılabilir.

Deep pagination gerekiyorsa:

```text
search_after + point in time (PIT)
```

tercih edilmelidir.

## 8. Index Sync

Elasticsearch entity save işlemi HTTP request transaction'ı içinde yapılmamalıdır.

Önerilen:

```text
PostgreSQL + Outbox
       ↓
Kafka
       ↓
Indexer Consumer
       ↓
Elasticsearch
```

Consumer idempotent olmalıdır.

## 9. Rebuild Strategy

Elasticsearch index tamamen silinse bile PostgreSQL'den yeniden üretilebilmelidir.

Admin/internal job:

```text
POST /internal/search/reindex/jobs
```

Bu endpoint public erişime açık olmamalıdır.
