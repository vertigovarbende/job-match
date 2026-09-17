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
- [x] Audit altyapısı (generic audit log mekanizması — bkz. AUDIT.md)
  - [x] `audit` modülü: `AuditAction`, `AuditableDomainEvent`, `AuditLog` domain modeli, `AuditRepository` portu, `AuditRepositoryAdapter`, generic `AuditEventListener`
  - [x] `jm_audit_log` tablosu (Flyway `V1__create_jm_audit_log_table.sql` — projenin ilk migration'ı)
- [x] Spring profilleri: application.yaml + application-dev.yaml / application-test.yaml / application-prod.yaml
- [x] Base security setup (bkz. Phase 1 — Spring Security + OAuth2 Resource Server, docs/IDENTITY.md madde 4-5)

## Phase 1 — Identity

- [x] Keycloak Docker Compose servisi
- [x] Realm ve client konfigürasyonu (import script / IaC)
- [x] Candidate / Employer / Admin rollerinin Keycloak'ta tanımlanması
- [x] Spring Security OAuth2 Resource Server entegrasyonu
- [x] JWT claim → ROLE_* authority mapping
  - [ ] Client rolü/group desteği — ertelendi, somut bir ince taneli izin ihtiyacı çıktığında ayrı bir madde olarak ele alınacak (bkz. docs/IDENTITY.md, Refactor Notu 2)
- [x] Uygulama içi user referans tablosu (Keycloak subject id ile)
- [x] Email doğrulama ve şifre sıfırlama akışlarının Keycloak üzerinden doğrulanması
- [x] Candidate: password + email verification (native)
  - [ ] Social login (Google/LinkedIn, identity broker) — ertelendi, gerçek OAuth client id/secret eldeyken ayrı bir madde/PR olarak ele alınacak (bkz. docs/IDENTITY.md madde 8.1)
- [x] Employer/Admin: password + TOTP (conditional flow ile role bazlı zorunlu)

## Phase 2 — Candidate & Company

> **Not (branch sıralama kararı):** Candidate ve Company maddeleri, identity/audit'in izlediği bottom-up yaklaşımla (domain → persistence → application) ayrı branch'lerde ilerliyor; REST katmanı bu maddelerin kapsamı dışında tutuluyor. Sıralama: candidate-profile → candidate-skills → company-profile → company-membership (dördü de tamamlandı, REST'siz; "Company verification" ayrı bir branch olarak açılmadı, company-profile'a gömüldü -- bkz. COMPANY.md) → REST (candidate + company, tek/ardışık bir aşamada, DTO/controller konvansiyonu bir kerede netleştirilir) → Phase 2 testleri (bkz. CANDIDATE_SKILLS.md'deki ertelenmiş test kararıyla aynı mantık). Bu yüzden aşağıdaki maddeler yalnızca domain/persistence/application tamamlandığında değil, REST katmanı da dahil tam bittiğinde işaretlenecek; ara ilerleme ilgili modülün kendi docs/*.md dosyasında izlenir.
>
> **Güncelleme:** yukarıdaki sıralama başlangıçta yalnızca candidate-profile → candidate-skills → company-profile → REST olarak yazılmıştı; company-membership branch'i o sırada henüz kararlaştırılmamıştı, sonradan ayrı bir branch olarak eklendi (bkz. COMPANY.md'nin "Employer-company membership tamamen bu branch'in dışında" notu ve COMPANY_MEMBERSHIP.md) ve tamamlandı. Bu not, gerçekleşen sırayı yansıtacak şekilde güncellendi.

- [x] Candidate profile
- [x] Skills
- [x] Experiences
- [x] Certifications
- [x] Languages
- [x] Company profile
- [x] Employer-company membership
- [x] Company verification
  - [x] Audit entegrasyonu: verification, çalışan ekleme/çıkarma (bkz. AUDIT.md)

## Phase 3 — Job Management

- [ ] Job draft creation
- [ ] Update
- [ ] Publish
- [ ] Close
- [ ] Archive
  - [ ] Audit entegrasyonu: publish/close/archive event'leri (bkz. AUDIT.md)
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
- [ ] Candidate search index (employer'ların adayları skill/experience/location'a göre arayabilmesi) — ertelendi, somut bir "candidate search" özelliği talebi çıkarsa değerlendirilecek; uygulanırsa Experience/Education/Certification/CandidateSkill/CandidateLanguage değişiklikleri candidate.events.v1 (bkz. EVENTS.md) üzerinden bu index'e senkronize edilir (candidate-skills tasarım sürecinde gündeme geldi, bkz. CANDIDATE_SKILLS.md)

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
  - [ ] Audit entegrasyonu: status geçişleri (bkz. AUDIT.md)

## Phase 7 — Matching

- [ ] Matching domain model
- [ ] Skill score
- [ ] Experience score
- [ ] Location score
- [ ] Salary score
- [ ] Explainable score breakdown
- [ ] Recommended jobs endpoint
- [ ] Event-driven skor yeniden hesaplama (CandidateSkillAttached/ExperienceAdded vb. event'lerle Phase 8'in cache'inde "recommended jobs" sonuçlarını arka planda invalidate/yeniden hesapla) — ertelendi, skorların senkron mu event-driven mi hesaplanacağına henüz karar verilmedi, somut bir performans problemi çıkarsa değerlendirilecek (candidate-skills tasarım sürecinde gündeme geldi, bkz. CANDIDATE_SKILLS.md)

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
- [ ] HTTP request/response audit logging (teknik/güvenlik amaçlı; AUDIT.md'deki iş kararı audit'inden bağımsız, ayrı bir konu) — her request/response'un IP, header, body, status code gibi detaylarının yakalanıp bir stream'e gönderilmesi; AWS Kinesis yok, ama biz zaten Kafka'yı benimsediğimizden (bkz. ADR-004/005, Phase 5) ikinci bir streaming sistemi/AWS kilitlenmesi eklemek yerine Kafka üzerinden yapılması daha tutarlı olur
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
