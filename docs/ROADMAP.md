# Roadmap

## Phase 0 — Foundation

- [ ] Repository setup
- [ ] Java/Spring Boot project
- [x] Test altyapısı: maven-surefire-plugin (unit, `mvn test`) + maven-failsafe-plugin (integration, `mvn verify`) ayrımı, `*IT.java` naming convention
- [ ] Clean Architecture package conventions
- [ ] Docker Compose
- [ ] PostgreSQL (local: Docker Compose, bkz. ADR-009)
- [ ] Flyway (local ve Neon direct connection ile test edilir)
- [ ] Global exception handling
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
