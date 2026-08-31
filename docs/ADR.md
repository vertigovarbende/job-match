# Architecture Decision Records

Bu dosya projenin önemli teknik kararlarının kısa kaydını tutar.

---

## ADR-001 — Modular Monolith First

**Status:** Accepted

### Decision

Sistem ilk sürümde microservice yerine modular monolith olarak geliştirilecektir.

### Rationale

- Domain sınırlarını öğrenmek için yeterlidir.
- Dağıtık transaction ve service discovery gibi gereksiz erken karmaşıklığı önler.
- Modül sınırları korunursa gelecekte extraction mümkündür.

---

## ADR-002 — PostgreSQL is Source of Truth

**Status:** Accepted

### Decision

Business entity state PostgreSQL'de tutulacaktır. Elasticsearch authoritative database değildir.

### Rationale

- Transactional consistency
- Relational constraints
- Reliable updates
- Search index'in yeniden üretilebilmesi

---

## ADR-003 — Elasticsearch as Search Read Model

**Status:** Accepted

### Decision

Job discovery ve full-text search Elasticsearch üzerinden yapılacaktır.

### Rationale

- Full-text relevance
- Faceted filtering
- Highlighting
- Typo tolerance
- Geo search

---

## ADR-004 — Async Index Synchronization

**Status:** Accepted

### Decision

PostgreSQL → Elasticsearch synchronization HTTP transaction içinde doğrudan yapılmayacaktır.

Kafka tabanlı asynchronous indexing kullanılacaktır.

---

## ADR-005 — Transactional Outbox

**Status:** Planned

### Decision

DB write ve event publish arasındaki dual-write problemine karşı Transactional Outbox pattern kullanılacaktır.

---

## ADR-006 — Feature-first Package Structure

**Status:** Accepted

### Decision

Proje global controller/service/repository package yapısı yerine feature/bounded-context bazlı paketlenecektir.

---

## ADR-007 — Matching Starts Deterministic

**Status:** Accepted

### Decision

İlk matching engine ML kullanmayacaktır.

Rule/weight tabanlı explainable matching kullanılacaktır.

### Rationale

- Debug edilebilir
- Test edilebilir
- Business rule değişiklikleri kolaydır
- ML modelinin gerçekten değer kattığı daha sonra ölçülebilir

---

## ADR-008 — Keycloak as Identity Provider

**Status:** Accepted

### Decision

Kimlik doğrulama ve token yönetimi için custom JWT issuance yerine Keycloak kullanılacaktır. Sistem, Keycloak'ı harici bir OAuth2/OIDC Identity Provider olarak kullanır; Spring Security bir OAuth2 Resource Server olarak Keycloak tarafından imzalanan access token'ları doğrular.

### Rationale

- Email doğrulama, şifre sıfırlama, refresh token rotation gibi akışlar hazır gelir; custom implementasyon riski azalır.
- OAuth2/OIDC standart bir protokoldür; ileride social login, SSO gibi ihtiyaçlar kolayca eklenebilir.
- Admin console üzerinden kullanıcı/rol yönetimi sağlanır.
- Password hashing, token imzalama gibi güvenlik kritik detaylar battle-tested bir sistem tarafından yönetilir.

### Consequences

- Docker Compose'a Keycloak servisi eklenir; local development bir bileşen daha içerir.
- Realm/client konfigürasyonu versiyon kontrolüne alınmalıdır (import script veya IaC).
- IDOR/ownership kontrolleri hâlâ application layer'da yapılmaya devam eder; Keycloak yalnızca authentication/authorization claim'lerini sağlar, business-level yetkilendirmeyi değil.
- Integration testlerinde gerçek Keycloak container'ı (Testcontainers ile) kullanılması önerilir.

---

## ADR-009 — Neon for Staging/Production PostgreSQL, Local Docker Compose for Development

**Status:** Accepted

### Decision

Local development ve integration testler local Docker Compose PostgreSQL (Testcontainers üzerinden) kullanır. Staging ve production ortamlarında Neon (serverless PostgreSQL) kullanılır.

### Rationale

- Local dev'de network bağımlılığı olmadan, offline çalışabilme ve hızlı reset/seed döngüsü sağlanır.
- Testcontainers ile local dev arasında aynı Postgres image/versiyonu kullanılarak local-CI-prod parity korunur.
- Neon free tier'ın sınırlı CU-saat bütçesi sık local/CI resetleriyle gereksiz tüketilmez.
- Neon'un branching özelliği (staging, PR-preview ortamları, prod-data debug) ve point-in-time restore'u production tarafında değerli kalır.

### Consequences

- Connection string yönetimi ortama göre değişir: local/Testcontainers'ta tek bir standart Postgres URL yeterlidir; Neon'da runtime için pooled (`-pooler`), migration/logical replication için direct connection string ayrımı yapılmalıdır.
- Postgres major version'ı local, Testcontainers ve Neon arasında hizalı tutulmalıdır.
- Staging/production deploy pipeline'ı Neon connection secret'larını (pooled + direct) ayrı ayrı yönetmelidir.

---

## ADR-010 — Rich Domain Model (Anemic Domain Model'den Kaçınma)

**Status:** Accepted

### Decision

Domain modelleri (Job, Candidate, Company, JobApplication, MatchResult vb.) yalnızca getter/setter barındıran veri taşıyıcıları (anemic domain model) olarak tasarlanmayacaktır. Business logic ve invariant'lar, ilgili oldukları domain modelinin kendi metodlarında yaşar.

### Rationale

- Anemic domain model'de business logic application/service katmanına sızar; entity yalnızca bir veri taşıyıcısına döner ve encapsulation kaybolur.
- Invariant'ların (ör. "DRAFT olmayan job tekrar publish edilemez", "salaryMin > salaryMax olamaz", "CLOSED ilana yeni application oluşturulamaz") domain modelinin içinde korunması, tutarsız state'lerin sistemde hiç oluşmamasını garanti eder.
- DOMAIN_MODEL.md'de zaten tanımlı olan `publish()`, `close()`, `archive()`, `updateDetails()` gibi davranışlar bu prensibin doğal bir sonucudur; bu ADR bunu resmi bir mimari kural olarak sabitler.

### Consequences

- Domain modellerinde public setter kullanılmaz; state değişiklikleri yalnızca anlamlı domain metodları üzerinden yapılır (ör. `job.publish()`, `application.withdraw()`), doğrudan alan ataması yapılmaz.
- Application/use-case servisleri "transaction script" gibi davranmaz: orkestrasyon yapar (port çağırma, transaction sınırı) ama business kararını (invariant kontrolü, state transition) domain modeline devreder.
- Domain modeli (domain/model) ile JPA persistence entity (infrastructure/persistence) ayrı tutulur — bkz. ARCHITECTURE.md Domain katmanı kuralları (@Entity, JpaRepository domain'de bulunmaz). İkisi arasında mapper (MapStruct) kullanılır; "entity" ifadesi DDD anlamında domain modelini, JPA `@Entity` ise yalnızca persistence detayını ifade eder.
- Value Object'ler (MatchScore, SalaryRange, Money vb.) kendi invariant'larını constructor'da doğrular (bkz. DOMAIN_MODEL.md `MatchScore` örneği).
- Code review'da yalnızca getter/setter'dan ibaret domain sınıfı veya business rule'un service katmanında if/else zinciriyle yürütülmesi pattern'i reddedilir.
