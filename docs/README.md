# JobMatch Platform

JobMatch, adaylar ile iş ilanlarını yetenek, deneyim, lokasyon ve çalışma tercihleri üzerinden eşleştiren; gelişmiş full-text arama ve filtreleme sunan Spring Boot tabanlı bir backend projesidir.

## Amaç

Bu proje yalnızca CRUD geliştirmek için değil; gerçek bir backend sisteminde aşağıdaki problemleri çözmek için tasarlanmıştır:

- Clean Architecture ile bağımlılıkların doğru yönlendirilmesi
- PostgreSQL'in source of truth olarak kullanılması
- Elasticsearch ile hızlı ve relevance tabanlı iş arama
- Kafka ile asenkron indeks senkronizasyonu
- Redis ile cache, rate limiting ve kısa ömürlü veriler
- Spring Security + JWT ile kimlik doğrulama ve yetkilendirme
- Eventual consistency ve hata toleransı
- Testcontainers ile gerçek altyapı bileşenleri üzerinde integration test
- Docker Compose ile lokal geliştirme ortamı

## Temel Kullanıcı Rolleri

### Candidate
- Profil oluşturur ve günceller.
- Skill, deneyim, eğitim ve tercih bilgilerini girer.
- İş ilanı arar ve filtreler.
- İlan kaydeder.
- İş ilanına başvurur.
- Kendisine uygun ilan önerilerini görüntüler.

### Employer
- Şirket profili yönetir.
- İş ilanı oluşturur, günceller ve yayından kaldırır.
- Başvuruları görüntüler.
- Adayları filtreler.
- İlan performansını takip eder.

### Admin
- Kullanıcı ve şirket moderasyonu yapar.
- Şüpheli ilanları yönetir.
- Sistem metriklerini ve audit kayıtlarını takip eder.

## Önerilen Teknolojiler

- Java 21+
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Elasticsearch
- Apache Kafka
- Redis
- Flyway
- MapStruct
- Testcontainers
- JUnit 5
- Mockito
- Docker / Docker Compose
- OpenAPI / Swagger
- Micrometer + Prometheus (opsiyonel)

## Ana Modüller

```text
jobmatch
├── identity
├── candidate
├── employer
├── job
├── application
├── search
├── matching
├── notification
└── shared
```

İlk sürüm modüler monolith olarak geliştirilebilir. Trafik ve organizasyon ihtiyacı oluşmadan mikroservise bölünmesi önerilmez.

## Mimari Prensip

```text
Presentation
    ↓
Application
    ↓
Domain
    ↑
Infrastructure Adapters
```

Domain katmanı Spring, JPA, Elasticsearch veya Kafka gibi teknolojileri bilmez.

## Veri Akışı Örneği

Bir employer yeni ilan oluşturduğunda:

```text
POST /api/v1/jobs
        ↓
CreateJobUseCase
        ↓
PostgreSQL Transaction
        ↓
JobCreatedEvent
        ↓
Outbox
        ↓
Kafka
        ↓
JobSearchIndexer
        ↓
Elasticsearch
```

Arama isteği ise doğrudan search use-case üzerinden Elasticsearch'e gider:

```text
GET /api/v1/jobs/search?q=java+spring
        ↓
SearchJobsUseCase
        ↓
JobSearchPort
        ↓
Elasticsearch Adapter
```

## MVP

MVP kapsamında:

1. Authentication & Authorization
2. Candidate profile
3. Company/Employer profile
4. Job posting management
5. Elasticsearch job search
6. Filtering and pagination
7. Job applications
8. Basic matching score
9. Kafka based indexing
10. Integration tests

## Dokümantasyon

- [Requirements](REQUIREMENTS.md)
- [Architecture](ARCHITECTURE.md)
- [Domain Model](DOMAIN_MODEL.md)
- [API Design](API.md)
- [Elasticsearch Design](ELASTICSEARCH.md)
- [Event & Kafka Design](EVENTS.md)
- [Security](SECURITY.md)
- [Testing Strategy](TESTING.md)
- [Roadmap](ROADMAP.md)
- [Architecture Decisions](ADR.md)
- [Contributing](CONTRIBUTING.md)
