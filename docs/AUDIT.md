# Audit Design

## Amaç

Sistemdeki iş açısından kritik state geçişlerinin ve kararların (kim, ne zaman, ne yaptı) kalıcı ve sorgulanabilir bir şekilde tutulması. Bu, entity'lerin her alan değişikliğinin (column-level diff) izlenmesi değil, anlamlı business action'ların izlenmesidir.

## Kapsam

Audit edilecek aksiyonlar:

- Job: `publish()`, `close()`, `archive()`
- JobApplication: status geçişleri (`SUBMITTED` → `UNDER_REVIEW` → `INTERVIEW` → `OFFER` → `REJECTED` / `WITHDRAWN` / `HIRED`), her bir geçiş ayrı ayrı
- Company: verification
- Company: çalışan ekleme / çıkarma
- Admin: moderation aksiyonları (kapsam ileride, ilgili feature somutlaştıkça netleşecek)

Kapsam dışı bırakılanlar:

- Identity/auth olayları (login, şifre sıfırlama, rol atama): Keycloak kendi event/audit log'unu tutuyor, uygulama tarafında duplication yapılmıyor (bkz. ADR-008).
- Candidate / Job / Company üzerindeki sıradan CRUD güncellemeleri (verification ve state-geçişleri hariç): business-critical karar/state-geçişi değil, sıradan profil düzenlemesi.
- Job `updateDetails()` gibi genel alan güncellemeleri: şimdilik kapsam dışı, ihtiyaç çıkarsa (ör. yayındaki bir ilanın kritik alanlarının değişmesi) ayrı bir karar olarak eklenebilir.

## Mimari Konum

Audit, `shared` modülüne değil, `candidate` / `company` / `identity` gibi kendi başına bir feature modülüne (`audit`) yerleştirilir. Gerekçe: `shared` bilinçli olarak minimal tutulur (bkz. ARCHITECTURE.md #3); audit'in kendi persistence'ı (`jm_audit_log` tablosu) ve ileride kendi query/API'si (audit trail görüntüleme) olacağı için bağımsız bir modül olarak ele alınması, `shared`'ın "her şeyin atıldığı çöp kutusu" (God/util package) anti-pattern'ine dönüşmesini engeller.

## Generic Mekanizma

Amaç: yeni bir audit edilecek aksiyon eklendiğinde audit altyapısına dokunmadan, yalnızca ilgili feature'da bir tanım eklenerek işin bitmesi.

### AuditAction

`ErrorCode` / ADR-011 pattern'inin aynısı uygulanır: bir `AuditAction` interface'i (`code()`, `description()`) tanımlanır, her feature kendi enum'unu yazar ve bu interface'i implement eder:

```text
AuditAction (interface)
├── JobAuditAction        (PUBLISHED, CLOSED, ARCHIVED, ...)
├── ApplicationAuditAction (STATUS_CHANGED, ...)
└── CompanyAuditAction     (VERIFIED, MEMBER_ADDED, MEMBER_REMOVED, ...)
```

Tek bir merkezi "God enum" oluşturulmaz; her feature kendi audit action'larının sahibi kalır.

### AuditableDomainEvent

Bir marker interface (`audit.domain.event`):

```java
public interface AuditableDomainEvent {
    String actorId();
    default String actorRole() { return null; }
    AuditAction action();
    String targetType();
    String targetId();
    default String correlationId() { return null; }
    Object details();
    Instant occurredAt();
}
```

`actorRole()` ve `correlationId()` bilinçli olarak `default` metod: çoğu event bu bilgiyi bilmez (correlation id özellikle request context'inden/MDC'den gelmesi daha doğal, bkz. Phase 9), bu yüzden zorunlu tutulmaz — elinde bu bilgi olan event'ler override edebilir.

Feature'ların fırlattığı domain event'ler (ör. `JobPublishedEvent`, `ApplicationStatusChangedEvent`) bu interface'i implement eder.

### AuditLog (domain model)

`audit.domain.model.AuditLog` — `AuditableDomainEvent`'ten türetilen (`AuditLog.from(event)`), framework'ten tamamen izole, immutable bir domain sınıfı (rich domain model prensibi, ADR-010). JPA persistence entity'sinden (`AuditLogEntity`) bilinçli olarak ayrı tutulur; ikisi arasındaki mapping infrastructure katmanında MapStruct ile yapılır (bkz. "MapStruct" bölümü aşağıda). Şimdilik `record` değil, sınıf (class) olarak tanımlanmıştır.

### AuditRepository (port)

`audit.domain.repository.AuditRepository` — yalnızca `void save(AuditLog auditLog)` içeren bir port. Repository port'ları bu projede `domain.repository` altında, "Port" soneki olmadan tanımlanır (bu, `audit` modülü inşa edilirken netleşen ve `ARCHITECTURE.md`'ye de yansıtılan genel bir konvansiyondur — yalnızca repository/persistence port'ları için geçerli, search/messaging gibi diğer outbound port'lar `application.port.out` altında kalmaya devam eder).

İmplementasyonu `audit.infrastructure.persistence.postgres.adapter.AuditRepositoryAdapter` — `AuditLog`'u `AuditLogEntityMapper` (MapStruct) ile `AuditLogEntity`'ye çevirir, `SpringDataAuditJpaRepository` ile kaydeder.

### MapStruct

`ARCHITECTURE.md` (§2) ve `ADR-010`, domain modeli ile JPA persistence entity'si arasındaki mapping'in MapStruct ile yapılacağını daha önceden dokümante etmişti, ancak bu şu ana kadar hiç uygulanmamıştı (projede ilk domain model / JPA entity çifti `AuditLog`/`AuditLogEntity`). Bu vesileyle MapStruct projeye eklendi:

- `mapstruct` + `mapstruct-processor` (1.6.3, Maven Central'daki güncel stabil sürüm; Spring Boot 4.1.1'in BOM'u MapStruct'ı yönetmiyor, versiyon elle belirtilir)
- Lombok ile birlikte kullanıldığı için `org.projectlombok:lombok-mapstruct-binding` (0.2.0) eklendi — Lombok 1.18.16+ ile MapStruct'ın aynı derleme adımında doğru sırayla annotation processing yapabilmesi için gereklidir (MapStruct'ın kendi FAQ'u bunu doğruluyor)
- `maven-compiler-plugin`'deki `annotationProcessorPaths` sırası önemli: önce `mapstruct-processor`, sonra `lombok`, en son `lombok-mapstruct-binding`
- `AuditLogEntityMapper`, `@Mapper(componentModel = "spring")` ile bir Spring bean'i olarak üretilir — **interface** olarak kalır (MapStruct'ın standart/idiyomatik kullanımı budur). `action` (AuditAction → String kod) `expression` ile elle map edilir. `details` (Object → Map, JSONB için) için ise mapper'ı bir abstract class'a çevirip `@Autowired` field eklemek yerine, bu dönüşümü ayrı bir `@Component` sınıfına (`AuditDetailsConverter`) taşıyıp MapStruct'ın `uses` mekanizmasıyla (`@Mapper(uses = AuditDetailsConverter.class)`) otomatik çözülmesini sağladık — mapper böylece hiçbir bağımlılık taşımadan saf bir interface kalabiliyor.
- **Önemli bir keşif:** Spring Boot 4, Jackson 3'e geçmiş durumda — `ObjectMapper`/`TypeReference` artık `com.fasterxml.jackson.*` değil, `tools.jackson.*` paketinden geliyor (`tools.jackson.databind.ObjectMapper`, `tools.jackson.core.type.TypeReference`). Yalnızca `jackson-annotations` modülü eski paket adını (`com.fasterxml.jackson.annotation`) korumuş — `ErrorResponse`'daki `@JsonInclude` importu bu yüzden hâlâ doğru. Bu, projede Jackson'ın `ObjectMapper`/`TypeReference` seviyesinde kullanılacağı her yerde akılda tutulmalı.

### Generic Listener

`audit.infrastructure.messaging.adapter.AuditEventListener` — tek bir generic `@TransactionalEventListener`, `AuditableDomainEvent` tipini yakalar, `AuditLog.from(event)` ile domain modeline çevirir ve `AuditRepository.save(...)` ile kaydeder. Yeni bir aksiyonu audit'e eklemek, yeni bir listener/servis kodu yazmayı değil, yalnızca ilgili event sınıfına `AuditableDomainEvent`'i implement ettirmeyi gerektirir.

`infrastructure.messaging` altına (persistence'a değil) bilinçli olarak yerleştirildi: event-driven bir mekanizma olduğu için mimari olarak "messaging" kategorisine giriyor. Şu an Kafka değil, in-process Spring application event'i kullanılıyor; ileride Kafka eklendiğinde bu paket Kafka publisher/consumer adapter'larıyla yan yana yaşayacak — audit'in Kafka'ya bağımlı olması gerekmiyor, yalnızca event'in taşınma şekli (in-process vs. Kafka) bir infrastructure detayı.

İki teknik detay bilinçli olarak elle ayarlandı:

- `phase = TransactionPhase.BEFORE_COMMIT` — `@TransactionalEventListener`'ın varsayılanı `AFTER_COMMIT`'tir, bu bizim "aynı transaction, aynı commit" tasarımımızla uyuşmaz; bu yüzden explicit olarak `BEFORE_COMMIT` set edildi.
- `fallbackExecution = true` — event aktif bir transaction olmadan publish edilirse (ör. ileride biri yanlışlıkla @Transactional olmayan bir yerden fırlatırsa), audit sessizce kaybolmak yerine yine de (transaction dışı olarak) yazılır. Audit kaybının, atomiklik garantisinin küçük bir esnemesinden daha kötü olduğu değerlendirildi.

## Transaction Zamanlaması

Audit kaydı, business transaction'ın **içinde**, aynı commit'te yazılır — `outbox_events` ile aynı yaklaşım (bkz. ADR-005): external I/O değil, aynı PostgreSQL transaction'ı içinde bir insert. Business aksiyon başarısız olur/rollback edilirse audit kaydı da yazılmaz; tutarlılık DB transaction sınırıyla garanti edilir.

Kafka'ya audit export etmek gibi bir ihtiyaç ileride çıkarsa (Phase 5+, Kafka altyapısı kurulduktan sonra), gerçek async outbox pattern'ine geçilebilir. Şimdilik buna gerek yoktur.

## Veritabanı Şeması

`jm_audit_log` tablosu (bkz. "jm_ öneki" not aşağıda):

> **jm_ öneki:** Uygulama tabloları `jm_` öneki ile oluşturulur (bkz. ARCHITECTURE.md #5) — audit_log tablosu için karar verilirken netleşen bir proje konvansiyonu.

```text
id              BIGSERIAL (PK)
occurred_at     TIMESTAMPTZ NOT NULL
actor_id        VARCHAR(255)  -- Keycloak subject id; sistem aksiyonları için nullable
actor_role      VARCHAR(100)  -- nullable
action          VARCHAR(100) NOT NULL   -- AuditAction.code(), ör. "JOB_PUBLISHED"
target_type     VARCHAR(100) NOT NULL   -- "JOB", "APPLICATION", "COMPANY"
target_id       VARCHAR(255) NOT NULL
correlation_id  VARCHAR(255)  -- Phase 9'daki correlation ID ile aynı, nullable şimdilik
details         JSONB         -- aksiyona özel serbest metadata (ör. eski/yeni status)
```

PK için UUID yerine BIGSERIAL tercih edildi: jm_audit_log dışarıya (public API) doğrudan expose edilen bir kaynak değil, yalnızca internal/admin amaçlı bir log; bu durumda BIGSERIAL hem daha basit hem de index/sıralama performansı açısından daha uygun.

**Kasıtlı olarak eklenmeyen constraint'ler:**

- `target_type`/`action` üzerinde sabit değer listesi dayatan bir CHECK constraint yok — bu, generic tasarımın amacını (yeni bir audit action eklemek için altyapıya/migration'a dokunmama) DB seviyesinde bozardı.
- `target_id` üzerinde foreign key yok — polymorphic bir referans (Job/Application/Company id'si olabilir), tek bir FK ile bağlanamaz; audit yazımı asla bir FK ihlali yüzünden başarısız olmamalı.
- `actor_id` üzerinde de foreign key yok — bir Keycloak subject id, yerel `users` tablosuna zorunlu referans değil (sistem aksiyonlarında null olabilir).
- `occurred_at` için `DEFAULT now()` eklendi — uygulama her zaman explicit değer set edecek, ama DB seviyesinde bir savunma hattı olarak.

**Append-only prensibi:** `jm_audit_log` tablosuna yalnızca `INSERT` yapılır, hiçbir zaman `UPDATE`/`DELETE` yapılmamalıdır. Bu, DB seviyesinde bir trigger ile zorlanmıyor (şimdilik gereksiz mühendislik); bunun yerine `AuditRepository` portunun yalnızca `save` metodu içermesi, `delete`/`update` metodlarının hiç eklenmemesi ile disiplin sağlanır.

İndeksler:

- `(target_type, target_id)` — bir kaydın tüm audit geçmişini çekmek için
- `(occurred_at)` — zaman bazlı sorgular için
- `(actor_id)` — opsiyonel

`details` alanı bilinçli olarak JSONB seçildi: aksiyona özel, serbest yapılı metadata tutulabilmesi için (ör. status değişiminde eski/yeni değer, member ekleme/çıkarmada hangi kullanıcı). Yapılandırılmış (her aksiyon için ayrı tablo) bir alternatif yerine bu tercih edildi çünkü generic'lik önceliklidir.

## Neden JaVers veya Hibernate Envers Değil

- **JaVers**: en son stabili (7.11.8) Spring Boot 4.0.0 + Hibernate 7.1.8.Final ile dokümante edilmiş uyumluluğa sahip; ancak bu proje Spring Boot 4.1.1 kullanıyor ve bu, Hibernate'i 7.4.5.Final'a sabitliyor — JaVers'ın dokümante ettiği baseline'dan daha yeni bir patch. Doğrulanmamış bir uyumluluk riski taşıyor.
- **Hibernate Envers**: Hibernate'in kendi alt modülü olduğu için versiyon uyumsuzluğu riski yok, ancak mimarisi ihtiyaca uymuyor: her entity için gölge `_AUD` tabloları + ayrı revision tablosu oluşturur, sorgulama JPQL değil ayrı bir `AuditReader` API'si gerektirir, ve yalnızca column-level diff yakalar — "iş anlamı" (hangi business action, kim, neden) kavramı yoktur.
- **Spring Data JPA'nın `@CreatedBy`/`@LastModifiedBy` auditing'i**: yalnızca mevcut satırın üzerine yazılan metadata'dır, geçmiş tutmaz — farklı bir kavramdır, bizim ihtiyacımızı karşılamaz.

Bu nedenlerle, projenin zaten birinci sınıf vatandaş olarak benimsediği domain event + outbox mimarisine (ADR-004, ADR-005) oturan custom bir audit log tercih edildi.

## Uygulama Adımları

Bu branch'in kapsamı yalnızca **generic audit altyapısıdır**. Job/Application/Company gibi feature'lara gerçek entegrasyon, ilgili feature'lar kendi fazlarında (Phase 2/3/6) implemente edildiğinde yapılacaktır — henüz bu feature'lar (dolayısıyla fırlatılacak gerçek domain event'ler) mevcut değil.

Adımlar sırayla, teker teker uygulanacak:

> **Not:** `V1__create_audit_log_table.sql`, projede yazılan ilk Flyway migration dosyasıdır (o ana kadar hiç migration yoktu, hiç `@Entity` de yoktu). Bu, ROADMAP.md Phase 0'daki açık "Flyway migration altyapısı" maddesini de fiilen başlatmış olur.

- [x] 1. Flyway migration: `jm_audit_log` tablosunun oluşturulması (`V1__create_jm_audit_log_table.sql` — projenin ilk Flyway migration'ı, bkz. not aşağıda; tablo adı sonradan `jm_` öneki konvansiyonu netleşince güncellendi)
- [x] 2. `audit` modülünün paket iskeletinin oluşturulması (`audit.domain`, `audit.application`, `audit.infrastructure.persistence`) — feature-first Clean Architecture konvansiyonuna uygun
- [x] 3. `AuditAction` interface'i (`audit.domain`)
- [x] 4. `AuditableDomainEvent` interface'i (`audit.domain`)
- [x] 5. `AuditLogEntity` JPA persistence entity'si (`audit.infrastructure.persistence`)
- [x] 6. `AuditLogRepository` (Spring Data JPA repository)
- [x] 7. Generic `AuditEventListener` (`@TransactionalEventListener`) — event'i entity'ye map edip repository ile kaydeder
- [x] 8. Doğrulama testi: henüz gerçek bir feature event'i (Job/Application/Company) olmadığından, test kaynağında basit bir test event + test action tanımlanıp, publish edildiğinde `jm_audit_log`'a satırın düştüğünü doğrulayan bir integration test (mevcut `TestContainerConfiguration` + Testcontainers PostgreSQL kullanılarak)
- [x] 9. `ROADMAP.md` güncellemesi:
  - Phase 0'a audit maddesinin eklenmesi ve tamamlandığında işaretlenmesi
  - Phase 2 (Company verification / Employer-company membership) altına ileride yapılacak audit entegrasyonu için bir alt-madde (bkz. AUDIT.md)
  - Phase 3 (Job Management — Publish/Close/Archive) altına ileride yapılacak audit entegrasyonu için bir alt-madde (bkz. AUDIT.md)
  - Phase 6 (Applications — Status workflow) altına ileride yapılacak audit entegrasyonu için bir alt-madde (bkz. AUDIT.md)

Gerçek feature entegrasyonları (Job publish/close/archive, Application status geçişleri, Company verification/member yönetimi) bu listenin dışındadır; ilgili feature'ların kendi branch'lerinde, kendi fazlarında yapılacaktır.
