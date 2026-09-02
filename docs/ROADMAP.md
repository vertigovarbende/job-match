# Roadmap

## Phase 0 — Foundation

- [ ] Repository setup (GitHub repo idaresi: branch protection, PR template vb. — bkz. CONTRIBUTING.md)
- [ ] Java/Spring Boot project
- [x] Test altyapısı: maven-surefire-plugin (unit, `mvn test`) + maven-failsafe-plugin (integration, `mvn verify`) ayrımı, `*IT.java` naming convention
  - [x] Persistence bağımlılıkları: `spring-boot-starter-data-jpa`, `postgresql` (runtime), `flyway-core` + `flyway-database-postgresql`, `spring-boot-testcontainers` + `testcontainers:postgresql` (test)
  - [x] `LogTrackerConfiguration` (`testsupport`) — Logback `ListAppender` tabanlı log assertion altyapısı, Spring'den bağımsız, `@AfterEach`'te appender detach edilir (appender leak'i önlemek için)
  - [x] `TestContainerConfiguration` (`testsupport`, `LogTrackerConfiguration`'ı extend eder) — tek seferlik (singleton) PostgreSQL container + `@ServiceConnection`; Flyway varsayılan olarak etkin (gerçek migration'lara karşı test), `ddl-auto: validate` `application-test.yaml`'da
- [ ] Clean Architecture package conventions
  - [x] `shared` modülü — ilk somut paket (bkz. ARCHITECTURE.md #3, #11 ve ADR-011)
    - [x] `BaseResponse` / `ErrorResponse` (`shared.presentation.response`)
    - [x] `ErrorCode` (`shared.domain`) + `CommonErrorCode` (`shared.presentation.exception`)
    - [x] `GlobalExceptionHandler` — Katman 1 (framework exception'ları: `MethodArgumentNotValidException`, `ConstraintViolationException`, `MethodArgumentTypeMismatchException`)
    - [x] `GlobalExceptionHandler` — Katman 1'e `HttpMessageNotReadableException` (+ `ErrorResponse.subErrors(InvalidFormatException)` overload'u), `HttpRequestMethodNotSupportedException`, `HttpMediaTypeNotSupportedException` eklendi
    - [x] `ErrorCode` interface'ine dördüncü metod (`header()`) eklenmesi + `CommonErrorCode`'un buna göre güncellenmesi
    - [x] `JobMatchException` kökü ve aile sınıfları (`DomainRuleViolationException`, `JobMatchResourceNotFoundException`, `JobMatchConflictException`, `JobMatchForbiddenException`, `JobMatchAuthenticationException`, `JobMatchProcessException`, `JobMatchInvalidArgumentException`)
    - [x] `GlobalExceptionHandler` — Katman 2 (aile bazlı handler'lar), Katman 3 (`JobMatchException` fallback, dinamik statü), Katman 4 (`Exception.class` catch-all → `GEN_001`)
    - [x] `GlobalExceptionHandlerTest` — 15 handler metodunun tamamı için birim testi (Ays referans projesindeki doğrudan-çağrı + oracle-stil assertion yaklaşımı baz alınarak; HTTP statü doğrulaması MockMvc gerektirmediği için yalnızca Katman 3'te (`ResponseEntity` üzerinden) yapılır)
- [ ] Persistence altyapısı (bkz. ADR-009)
  - [x] Docker Compose: PostgreSQL container (local development; diğer servisler kendi fazlarında eklenir)
  - [x] `spring-boot-starter-data-jpa`, `postgresql` (runtime), `flyway-core` + `flyway-database-postgresql` bağımlılıkları (pom.xml)
  - [ ] Flyway migration altyapısı (local Docker Compose ve Neon direct connection ile test edilir)
- [x] Spring profilleri: application.yaml + application-dev.yaml / application-test.yaml / application-prod.yaml
- [ ] Base security setup

## Phase 1 — Identity

- [ ] Keycloak Docker Compose servisi
- [ ] Realm ve client konfigürasyonu (import script / IaC)
- [ ] Candidate / Employer / Admin rollerinin Keycloak'ta tanımlanması
- [ ] Spring Security OAuth2 Resource Server entegrasyonu
- [ ] JWT claim → ROLE_* authority mapping
- [ ] Uygulama içi user referans tablosu (Keycloak subject id ile)
- [ ] Email doğrulama ve şifre sıfırlama akışlarının Keycloak üzerinden doğrulanması
- [ ] Candidate: password + email verification (native) + social login (Google/LinkedIn, identity broker)
- [ ] Employer/Admin: password + TOTP (conditional flow ile role bazlı zorunlu)

## Phase 2 — Candidate & Company

- [ ] Candidate profile
- [ ] Skills
- [ ] Experiences
- [ ] Company profile
- [ ] Employer-company membership

## Phase 3 — Job Management

- [ ] Job draft creation
- [ ] Update
- [ ] Publish
- [ ] Close
- [ ] Archive
- [ ] Ownership authorization
- [ ] Job REST API için Spring Cloud Contract (Groovy DSL) contract testleri

## Phase 4 — Elasticsearch Search

- [ ] Elasticsearch integration
- [ ] Job index mapping
- [ ] Keyword search
- [ ] Filters
- [ ] Sorting
- [ ] Pagination
- [ ] Highlighting
- [ ] Autocomplete
- [ ] Fuzzy search

## Phase 5 — Kafka & Outbox

- [ ] Outbox table
- [ ] Outbox publisher
- [ ] Kafka topics
- [ ] Search index consumer
- [ ] Retry
- [ ] Dead letter topic
- [ ] Idempotency
- [ ] job.events.v1 producer'ları için Spring Cloud Contract (Groovy DSL) contract testleri

## Phase 6 — Applications

- [ ] Apply job
- [ ] Duplicate prevention
- [ ] Candidate application history
- [ ] Employer applicant list
- [ ] Status workflow

## Phase 7 — Matching

- [ ] Matching domain model
- [ ] Skill score
- [ ] Experience score
- [ ] Location score
- [ ] Salary score
- [ ] Explainable score breakdown
- [ ] Recommended jobs endpoint

## Phase 8 — Redis

- [ ] Search suggestions cache
- [ ] Rate limiting
- [ ] Frequently accessed reference data cache

## Phase 9 — Production Readiness

- [ ] Structured logging
- [ ] Correlation ID
- [ ] Metrics
- [ ] Health checks
- [ ] Elasticsearch reindex tooling
- [ ] Neon staging/production ortam kurulumu (pooled + direct connection string, branching, autosuspend/always-on kararı)
- [ ] Backup strategy (Neon point-in-time restore)
- [ ] CI/CD
- [ ] Load tests

## Phase 10 — Advanced Features

- [ ] Saved jobs
- [ ] Saved searches
- [ ] Email notifications
- [ ] Geo search
- [ ] Synonym management
- [ ] Search analytics
- [ ] Employer dashboard
- [ ] Candidate ranking
- [ ] ML-assisted ranking experiment

## Phase 11 — Advanced Authentication

- [ ] Admin için WebAuthn/Passkeys (native, Keycloak 26.4+)
- [ ] Employer için WebAuthn/Passkeys (opsiyonel)
- [ ] Step-up / conditional authentication flow'larının genişletilmesi (ör. hassas işlemler için ek doğrulama)
- [ ] Email OTP custom authenticator SPI (gerçek kullanıcı ihtiyacı ölçülürse)
- [ ] SMS OTP custom authenticator SPI + SMS gateway entegrasyonu (gerçek ihtiyaç ölçülürse; telefon numarası alanı ve KVKK değerlendirmesi gerektirir)
