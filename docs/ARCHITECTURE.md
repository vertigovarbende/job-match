# Architecture

## 1. Architecture Style

Proje Clean Architecture prensiplerine uygun bir **modüler monolith** olarak başlatılır.

Amaçlar:

- Domain'i framework bağımlılıklarından izole etmek
- Use-case'leri controller ve persistence detaylarından ayırmak
- Elasticsearch/PostgreSQL/Kafka implementasyonlarının değiştirilebilir olması
- Test edilebilirliği artırmak

## 2. Katmanlar

### Domain

Saf business logic içerir. **Rich domain model** prensibi uygulanır — anemic domain model'den kaçınılır (bkz. ADR-010).

Örnek:

```text
Job
Candidate
Application
Skill
MatchingScore
```

Bu sınıflar yalnızca getter/setter içeren veri taşıyıcıları değildir; invariant'ları ve davranışları (`publish()`, `close()`, `updateDetails()` vb.) kendi içlerinde barındırırlar (bkz. DOMAIN_MODEL.md).

Domain katmanında şunlar bulunmaz:

- @Entity
- JpaRepository
- ElasticsearchRepository
- RestController
- KafkaTemplate
- RedisTemplate

Bu nedenle domain modeli (DDD anlamında "entity") ile JPA persistence entity (`@Entity` sınıfı, infrastructure/persistence katmanında) ayrı sınıflardır; aralarında MapStruct ile mapping yapılır. Domain modelindeki zengin davranış, JPA'nın persistence kaygılarıyla karışmaz.

### Application

Use-case orchestration içerir.

Örnekler:

```text
CreateJobUseCase
PublishJobUseCase
SearchJobsUseCase
ApplyToJobUseCase
CalculateMatchUseCase
```

Input/output portlar bu katmanda tanımlanır.

### Infrastructure

Teknoloji implementasyonları burada bulunur:

- PostgreSQL adapter
- Elasticsearch adapter
- Kafka publisher/consumer
- Redis adapter
- Email provider

### Presentation

HTTP API katmanıdır.

- Controllers
- Request DTO
- Response DTO
- Validation
- Exception mapping

## 3. Önerilen Paket Yapısı

Feature-first yaklaşım önerilir:

```text
com.example.jobmatch
│
├── job
│   ├── domain
│   │   ├── model
│   │   └── service
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   └── out
│   │   └── service
│   ├── infrastructure
│   │   ├── persistence
│   │   ├── search
│   │   └── messaging
│   └── presentation
│       └── rest
│
├── candidate
├── company
├── application
├── matching
├── identity
└── shared
```

`controller/service/repository/entity` şeklinde tüm sistemi yatay bölmek yerine bounded context/feature bazlı paketleme tercih edilir.

## 4. Port Örneği

```java
public interface JobRepositoryPort {
    Job save(Job job);
    Optional<Job> findById(JobId id);
}
```

```java
public interface JobSearchPort {
    SearchResult<JobSearchView> search(JobSearchQuery query);
}
```

Application service yalnızca interface'leri bilir.

## 5. PostgreSQL ve Elasticsearch Ayrımı

### PostgreSQL

Transactional business state:

- users
- candidates
- companies
- jobs
- applications
- skills
- candidate_skills
- job_skills
- outbox_events

### Elasticsearch

Denormalize edilmiş read/search model:

```text
job-search-v1
```

Elasticsearch kaydı doğrudan domain entity olarak kabul edilmez.

## 6. Transaction Boundary

Yanlış yaklaşım:

```text
DB transaction aç
→ Job save
→ Elasticsearch call
→ Kafka call
→ transaction commit
```

Bu yaklaşım external I/O nedeniyle transaction süresini uzatır ve coupling yaratır.

Önerilen:

```text
DB transaction
  → Job save
  → OutboxEvent save
COMMIT

Outbox publisher
  → Kafka

Kafka consumer
  → Elasticsearch
```

## 7. Modüler Monolith Sınırları

Modüller birbirlerinin repository implementasyonlarına erişmemelidir.

Örneğin Candidate modülü Job tablosunu direkt query etmek yerine gerekirse application-level port kullanmalıdır.

## 8. Sonradan Mikroservise Ayrılabilecek Modüller

Gerekirse gelecekte:

- Search Service
- Matching Service
- Notification Service

ayrılabilir.

Ancak ilk sürümde dağıtık sistem karmaşıklığı eklenmemelidir.


## 9. Identity Provider Entegrasyonu (Keycloak)

Kimlik doğrulama Keycloak üzerinden yapılır (bkz. ADR-008). Bu, `identity` modülünün sorumluluğunu değiştirir:

```text
Presentation
    ↓ (Bearer JWT)
Spring Security OAuth2 Resource Server
    ↓ (JWK Set ile imza doğrulama)
Application
```

`identity` modülü artık kendi password/token issuance kodunu barındırmaz; bunun yerine:

- Keycloak'tan gelen JWT claim'lerini (sub, roles, email) domain'e mapler.
- Uygulama-spesifik kullanıcı verisini (candidate/employer ilişkisi gibi) Keycloak `sub` (subject) id'sine referansla PostgreSQL'de tutar.
- Realm/client rollerini (`candidate`, `employer`, `admin`) `ROLE_*` authority'lerine map eder.

PostgreSQL'deki `users` tablosu artık password hash tutmaz; yalnızca Keycloak subject id ile uygulama içi profil/rol ilişkisini tutan bir referans tablosuna dönüşür.

## 10. Ortam Bazlı PostgreSQL Stratejisi

PostgreSQL farklı ortamlarda farklı şekilde çalıştırılır (bkz. ADR-009):

```text
Local development    → Docker Compose PostgreSQL
Integration tests    → Testcontainers PostgreSQL (aynı image/versiyon)
Staging / Production → Neon (serverless PostgreSQL)
```

Neon kullanılan ortamlarda iki farklı connection string ayrımı yapılmalıdır:

- **Pooled connection** (`-pooler` hostname): Uygulama runtime'ı (HikariCP) için kullanılır; serverless/short-lived connection pattern'ine uygundur.
- **Direct connection**: Flyway migration'ları, `pg_dump` ve (ileride kullanılırsa) logical replication/CDC için kullanılır. PgBouncer'ın transaction pooling modu DDL ve advisory lock içeren işlemlerle uyumlu değildir.

Local ve Testcontainers ortamlarında bu ayrım gerekmez; tek bir standart Postgres connection string yeterlidir.
