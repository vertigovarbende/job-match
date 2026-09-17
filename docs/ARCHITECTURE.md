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

Saf business logic içerir. **Rich domain model** prensibi uygulanır — anemic domain model'den kaçınılır (bkz. ADR-010). Yeni domain modeli/use-case/VO tasarımında SOLID/cohesion/coupling sorularının sistematik olarak ele alınması gerekir (bkz. ADR-012). Projede kullanılan/kullanılması planlanan tasarım desenlerinin kataloğu için bkz. DESIGN_PATTERNS.md.

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
│   │   ├── service
│   │   └── repository        → repository port'ları (örnek: JobRepository, "Port" soneki kullanılmaz)
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   └── out           → repository dışındaki outbound port'lar (search, messaging vb.)
│   │   └── service
│   ├── infrastructure
│   │   ├── persistence
│   │   │   └── postgres
│   │   │       ├── entity      → JPA persistence entity'leri
│   │   │       ├── repository  → Spring Data JPA repository'leri (teknik detay)
│   │   │       ├── mapper      → domain model <-> JPA entity mapping (MapStruct)
│   │   │       └── adapter     → domain.repository port'unu implement eden adapter (örnek: JobRepositoryAdapter)
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
├── audit
└── shared
```

Repository port'larının `domain.repository` altında (application.port.out yerine) tanımlanması ve implementasyonlarının `<Feature>RepositoryAdapter` (örnek: `JobRepositoryAdapter`) olarak adlandırılması, `audit` modülü inşa edilirken netleştirilen bir konvansiyondur (bkz. docs/AUDIT.md). Bu yalnızca repository (persistence) port'ları için geçerlidir; search/messaging gibi diğer outbound port'lar `application.port.out` altında kalmaya devam eder.

`controller/service/repository/entity` şeklinde tüm sistemi yatay bölmek yerine bounded context/feature bazlı paketleme tercih edilir.

`shared` modülü, tek bir feature/bounded context'e ait olmayan cross-cutting concern'leri barındırır:

```text
shared
├── presentation
│   ├── response
│   │   ├── BaseResponse             → başarılı response'ların ortak zarfı (bkz. API.md)
│   │   └── ErrorResponse            → hata response'larının ortak şeması (bkz. API.md)
│   └── exception
│       ├── CommonErrorCode          → framework/altyapı seviyesinde oluşan genel hata kodları
│       └── handler
│           └── GlobalExceptionHandler → tüm modüllerin fırlattığı exception'ları ErrorResponse formatına çevirir (bkz. API.md, ADR-011)
└── domain
    ├── ErrorCode                         → her modülün kendi ErrorCode enum'unun implemente ettiği, framework'ten bağımsız kontrat
    ├── JobMatchException                 → tüm custom exception'ların ortak, nötr kökü (bkz. ADR-011)
    ├── DomainRuleViolationException      → iş kuralı/invariant ihlalleri ailesi (422)
    ├── JobMatchResourceNotFoundException → "kaynak bulunamadı" ailesi (404)
    ├── JobMatchConflictException         → çakışma ailesi (409)
    ├── JobMatchForbiddenException        → yetkilendirme ailesi (403)
    ├── JobMatchAuthenticationException   → kimlik doğrulama ailesi (401)
    ├── JobMatchProcessException          → süreç/state-machine ihlalleri ailesi (409)
    └── JobMatchInvalidArgumentException  → geçersiz/beklenmeyen argüman ailesi (400)
```

Modül-özel exception'lar (ör. `job.domain.JobNotFoundException`) ilgili aileyi extend eder ve kendi modülünün `ErrorCode`'unu (ör. `JobErrorCode.JOB_NOT_FOUND`) taşır — bkz. ADR-011.

`shared` bilinçli olarak minimal tutulur — modül-spesifik business logic (job/candidate/company vb.) buraya taşınmaz, yalnızca gerçekten tüm modüller arasında paylaşılan, feature'a özgü olmayan kod barındırır. Aksi halde "her şeyin atıldığı çöp kutusu" (God/util package) anti-pattern'ine dönüşür.

## 4. Port Örneği

Repository (persistence) port'ları `domain.repository` altında, "Port" soneki olmadan tanımlanır; implementasyonları `infrastructure.persistence.<teknoloji>.adapter` altında `<Feature>RepositoryAdapter` olarak adlandırılır (örnek: `JobRepositoryAdapter`, bkz. docs/AUDIT.md):

```java
public interface JobRepository {
    Job save(Job job);
    Optional<Job> findById(JobId id);
}
```

Repository dışındaki outbound port'lar (search, messaging vb.) `application.port.out` altında kalmaya devam eder:

```java
public interface JobSearchPort {
    SearchResult<JobSearchView> search(JobSearchQuery query);
}
```

Application service yalnızca interface'leri bilir.

## 5. PostgreSQL ve Elasticsearch Ayrımı

### PostgreSQL

Uygulama tabloları `jm_` öneki ile oluşturulur (bkz. docs/AUDIT.md — bu konvansiyon audit_log tablosu için karar verilirken netleştirilmiştir).

Transactional business state:

- jm_users
- jm_candidates
- jm_companies
- jm_jobs
- jm_applications
- jm_skills
- jm_candidate_skills
- jm_job_skills
- jm_outbox_events
- jm_audit_log

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

Spring profilleri:

```text
dev  → local development (senin bilgisayarın, Docker Compose)
test → otomatik testler (Testcontainers ile)
prod → staging/production (Neon)
```

`application.yaml` (ortak ayarlar) + `application-{profile}.yaml` (profile'a özel override) yapısı kullanılır; aktif profil `SPRING_PROFILES_ACTIVE` ortam değişkeni ile seçilir (bkz. `.env` / `.env.example`).

## 11. Custom Exception Hiyerarşisi ve GlobalExceptionHandler Stratejisi

Tüm custom (framework olmayan) exception'lar `shared.domain.JobMatchException` adlı nötr, boş bir kökten türer (bkz. ADR-011). Bu kökten yedi kardeş "aile" sınıfı türetilir:

```text
JobMatchException
├── DomainRuleViolationException      → iş kuralı/invariant ihlalleri (422)
├── JobMatchResourceNotFoundException → kaynak bulunamadı (404)
├── JobMatchConflictException         → çakışma (409)
├── JobMatchForbiddenException        → yetkilendirme hatası (403)
├── JobMatchAuthenticationException   → kimlik doğrulama hatası (401)
├── JobMatchProcessException          → süreç/state-machine ihlali (409)
└── JobMatchInvalidArgumentException  → domain seviyesinde geçersiz argüman (400)
```

Her modül kendi somut exception'larını uygun aileden türetir, örneğin:

```text
job.domain.JobNotFoundException extends JobMatchResourceNotFoundException
candidate.domain.CandidateNotFoundException extends JobMatchResourceNotFoundException
```

Her somut exception, kendi modülünün `ErrorCode` implementasyonunu (ör. `JobErrorCode.JOB_NOT_FOUND`) taşır; `GlobalExceptionHandler` bu `ErrorCode`'dan `code`/`message`'ı okuyarak `ErrorResponse` üretir.

Konvansiyon: `JobMatchException` ve her aile sınıfı (root dahil), `Serializable` olan `RuntimeException`'dan türediği için kendi `serialVersionUID`'sini bildirir (`@Serial private static final long serialVersionUID = 1L;` — gerçek bir cross-version serialization ihtiyacı yok, yalnızca IDE/statik analiz uyarılarını (ör. SonarQube `java:S2057`) susturmak içindir, bu yüzden anlamlı bir hash yerine sabit `1L` kullanılır). Bu konvansiyon, ileride yazılacak modül-özel somut exception sınıfları (ör. `JobNotFoundException`) için de geçerlidir — `serialVersionUID` inherit olmadığından her sınıf kendi alanını tanımlamalıdır.

> **Not:** `JobMatchProcessException` ile `DomainRuleViolationException` arasındaki sınır bazı durumlarda belirsiz olabilir (ör. bir state-machine geçiş kuralı aynı zamanda bir domain invariant'ı da ihlal edebilir). Yeni bir exception yazılırken hangi aileye ait olduğuna açıkça karar verilmelidir — bkz. ADR-011 Consequences.

### GlobalExceptionHandler — 4 Katmanlı Strateji

`GlobalExceptionHandler`, exception'ları dört katmanda ele alır (bkz. ADR-011):

| Katman | Kapsam | Statü belirleme |
|---|---|---|
| 1 | Framework exception'ları (`MethodArgumentNotValidException`, `ConstraintViolationException`, `MethodArgumentTypeMismatchException`, `HttpMessageNotReadableException` → 400, `HttpRequestMethodNotSupportedException` → 405, `HttpMediaTypeNotSupportedException` → 415) | Statik `@ResponseStatus` |
| 2 | Aile bazlı custom exception'lar (`DomainRuleViolationException` → 422, `JobMatchResourceNotFoundException` → 404, `JobMatchConflictException` → 409, `JobMatchForbiddenException` → 403, `JobMatchAuthenticationException` → 401, `JobMatchProcessException` → 409, `JobMatchInvalidArgumentException` → 400) | Statik `@ResponseStatus` (ailenin statüsü sabittir) |
| 3 | Genel `JobMatchException` fallback'i (hiçbir aileye oturmayan durumlar) | Dinamik — `ResponseEntity` + `HttpStatus.valueOf(exception.getErrorCode().status())` |
| 4 | Gerçekten beklenmeyen exception'lar (`Exception.class`) | Statik `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)`, `CommonErrorCode.INTERNAL_SERVER_ERROR` |

Katman 3, `ErrorCode.status()`'a (framework'ten bağımsız `int`) ilk kez gerçek bir işlev kazandırır — Katman 1/2'de statü sabit olduğundan `@ResponseStatus` yeterlidir, ama Katman 3'te statü yalnızca runtime'da elimizdeki `ErrorCode`'dan okunabilir. Spring, `@ExceptionHandler` seçimini class içindeki tanım sırasına göre değil, fırlatılan exception'ın hiyerarşisindeki en spesifik eşleşen tipe göre yaptığı için katmanların fiziksel sırası önemli değildir.

`ErrorCode` interface'i dört metod içerir: `code()`, `header()`, `status()` (framework'ten bağımsız `int`) ve `defaultMessage()`. `header()`, generic (aile/fallback) handler'ların `exception.getErrorCode().header()` üzerinden tutarlı bir kategori etiketi okuyabilmesini sağlar; artık `CommonErrorCode`'un enum olmasından gelen `.name()`'e bağımlı değildir.
