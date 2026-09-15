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

---

## ADR-011 — Custom Exception Hiyerarşisi ve Katmanlı (Tiered) Global Exception Handling

**Status:** Accepted

### Decision

Tüm custom (framework olmayan) exception'lar, nötr ve boş bir kök sınıf olan `JobMatchException`'ı extend eder. Bu kökten, her biri belirli bir hata kategorisini temsil eden kardeş "aile" sınıfları türer: `DomainRuleViolationException` (iş kuralı/invariant ihlalleri, 422), `JobMatchResourceNotFoundException` (404), `JobMatchConflictException` (409), `JobMatchForbiddenException` (403), `JobMatchAuthenticationException` (401), `JobMatchProcessException` (süreç/state-machine ihlalleri, 409) ve `JobMatchInvalidArgumentException` (400 — domain seviyesinde fırlatılan geçersiz argüman durumları, framework seviyesi Bean Validation'dan ayrı). Her modül, kendi somut exception'larını (ör. `job.domain.JobNotFoundException`) ilgili aileden türetir ve kendi modül-özel `ErrorCode` enum'unu (ör. `JobErrorCode.JOB_NOT_FOUND`) taşır.

`GlobalExceptionHandler`, exception'ları dört katmanda ele alır:

1. **Framework exception'ları** (`MethodArgumentNotValidException`, `ConstraintViolationException`, `MethodArgumentTypeMismatchException` vb.) — her biri için ayrı, statik `@ResponseStatus` kullanan handler.
2. **Aile bazlı custom exception'lar** — her aile (`JobMatchResourceNotFoundException`, `JobMatchConflictException` vb.) için tek bir handler; statü ailenin doğası gereği sabit olduğundan yine statik `@ResponseStatus` kullanılır.
3. **Genel `JobMatchException` fallback'i** — hiçbir aileye oturmayan ama yine de bir `ErrorCode` taşıyan durumlar için; statü derleme zamanında bilinemediğinden `ResponseEntity` ile `HttpStatus.valueOf(exception.getErrorCode().status())` kullanılarak dinamik olarak set edilir.
4. **Gerçekten beklenmeyen exception'lar** (`Exception.class`) — `CommonErrorCode.INTERNAL_SERVER_ERROR` (`GEN_001`) ile 500 döner, statik `@ResponseStatus`.

Spring, `@ExceptionHandler` seçimini class içindeki tanım sırasına göre değil, fırlatılan exception'ın hiyerarşisindeki en spesifik eşleşen tipe göre yaptığından, katmanların fiziksel sırası önemli değildir.

`ErrorCode` interface'i (`shared.domain.ErrorCode`) dört metod içerir: `code()`, `header()`, `status()` (framework'ten bağımsız `int`) ve `defaultMessage()`. `header()` metodu, generic (aile/fallback) handler'ların `exception.getErrorCode().header()` üzerinden tutarlı bir kategori etiketi okuyabilmesi için eklenmiştir; öncesinde bu bilgi yalnızca `CommonErrorCode`'un enum olmasından gelen `.name()` ile elde edilebiliyordu ve bu, interface'in bir parçası değildi.

### Rationale

- `DomainRuleViolationException`'ı tüm exception'ların ortak köküne dönüştürmek yerine kardeş bir aile olarak bırakmak, isimlendirmenin anlamını korur — "forbidden" veya "authentication" bir domain rule violation değildir.
- Aile bazlı handler'lar, her modülün her exception'ı için ayrı ayrı `GlobalExceptionHandler` metodu yazma ihtiyacını ortadan kaldırır; yeni bir modül exception'ı doğru aileyi extend ettiği sürece otomatik olarak doğru HTTP status'a eşlenir.
- Katman 3 (genel `JobMatchException` fallback'i), `ErrorCode.status()` alanına ilk kez gerçek bir işlev kazandırır; Katman 1/2'de statü sabit olduğu için `@ResponseStatus` yeterliyken, Katman 3'te statü yalnızca runtime'da elimizdeki `ErrorCode`'dan okunabilir.
- Katman 4, framework dışı/öngörülemeyen hatalarda (ör. `NullPointerException`) API tüketicisinin hâlâ tutarlı bir `ErrorResponse` formatı görmesini garanti eder; bu katman olmadan böyle hatalar Spring'in varsayılan `/error` formatına düşerdi.

### Consequences

- `JobMatchProcessException` ile `DomainRuleViolationException` arasındaki sınır bazı durumlarda belirsiz olabilir (ör. bir state-machine geçiş kuralı aynı zamanda bir domain invariant'ı da ihlal edebilir); her modül kendi exception'ını yazarken hangi aileye ait olduğuna açıkça karar vermelidir.
- `GlobalExceptionHandler` içinde bazı handler metodları `ErrorResponse` (statik statü), bazıları `ResponseEntity<ErrorResponse>` (dinamik statü) döner — bu kasıtlı bir tutarsızlıktır, her handler'ın kendi statü belirleme ihtiyacına göre seçilmiştir.
- Her modülün kendi `ErrorCode` enum'unu ve somut exception sınıflarını tanımlarken doğru aileyi seçmesi, code review'da kontrol edilmesi gereken bir noktadır.

---

## ADR-012 — Domain Model, Use-Case ve Value Object Tasarımında Sistematik SOLID/Cohesion/Coupling İncelemesi

**Status:** Accepted

### Decision

Yeni bir domain modeli, use-case (port/in + Command + Service) veya value object tasarlanırken/kodlanırken, kod yazmaya geçmeden önceki tartışma aşamasının standart bir parçası olarak şu sorular sorulur: sınıfın/arayüzün tek bir sorumluluğu var mı (SRP); encapsulation gerçekten kendini koruyor mu (ör. invariant'ı bypass edebilecek bir public setter/method var mı); modüller ve aggregate'ler arası coupling ID-only mu kalıyor yoksa gereksiz bir object-graph bağımlılığı mı ekleniyor; ve bir soyutlama (ortak taban sınıf, generic tip, paylaşılan interface) gerçek/paylaşılan bir kavramı mı yoksa bugünkü tesadüfi bir yapısal benzerliği mi genelliyor. Bu inceleme yalnızca `candidate` modülüyle sınırlı değildir — `docs/ROADMAP.md`'deki tüm fazlarda (Job, Company, Application, Matching vb.) yazılacak her yeni domain/use-case/VO için geçerlidir.

### Rationale

- `feature/phase-02/candidate-skills` branch'i tamamlandıktan sonra yapılan bir UML class diagram incelemesi (bkz. docs/CANDIDATE_SKILLS.md, "Ek Düzeltme" bölümleri), kod yazılıp bittikten SONRA fark edilen iki gerçek sorun ortaya çıkardı: (1) tüm aggregate root'larda (`Candidate` dahil) blanket Lombok `@Setter` kullanımı, `update(...)` metodlarının sağladığı invariant korumasını bypass edilebilir hale getiriyordu — bu ayrıca ADR-010'un "domain modellerinde public setter kullanılmaz" kuralının fiilen ihlal edildiği anlamına geliyordu; (2) `Experience`/`Education`'ın `isCurrent()`'ı birebir aynıydı (essential duplication, bir `Ongoing` interface'iyle düzeltildi), `CandidateSkill`/`CandidateLanguage`'ın yapısal ikizliği ise bilinçli olarak birleştirilmedi (accidental duplication).
- Bu tür sorunları kod yazıldıktan sonra bir "code review" adımında yakalamak yerine, madde/karar tartışması sırasında (yani bu projenin zaten izlediği "önce tartış, docs/*.md'ye yaz, sonra kodla" disiplininin bir parçası olarak) daha erken yakalamak, hem düzeltme maliyetini düşürür hem de zaten var olan ADR-010 gibi kararların fiilen uygulanmasını garanti eder.
- "Essential vs. accidental duplication" ayrımı, DRY'ı körü körüne uygulamanın (her benzerliği zorla bir taban sınıfa/generic tipe çekmenin) tip güvenliğini ve gelecekteki ayrışma esnekliğini bozabileceğini fark ettirdi (bkz. generic UseCase'in ve `Guard` sınıfının reddedilme gerekçeleriyle aynı mantık, docs/CANDIDATE_SKILLS.md).

### Consequences

- Yeni bir domain modeli/use-case/VO tasarım tartışmasında (docs/*.md'ye karar yazılırken), yukarıdaki dört soru açıkça ele alınmalıdır; bu, code review'da ayrıca kontrol edilmesi gereken bir kontrol listesi maddesi haline gelir (ADR-010'un Consequences'ındaki "yalnızca getter/setter'dan ibaret domain sınıfı reddedilir" kuralının bir üst kümesi).
- Lombok `@Setter`, domain modeli sınıflarında (`domain/model` altında) kullanılmaz — yalnızca `@Getter` ve gerekliyse `@SuperBuilder`; mutasyon yalnızca anlamlı domain metodları (`update(...)`, `updateProficiency(...)` vb.) üzerinden yapılır. Bu, ADR-010'un zaten var olan "public setter kullanılmaz" kuralının Lombok-spesifik netleştirmesidir.
- Bir soyutlama (taban sınıf, generic tip) önerildiğinde, "bu iki/daha fazla sınıf gerçekten AYNI kavramı mı temsil ediyor, yoksa bugün şans eseri mi benziyor" sorusu açıkça tartışılmalı ve docs/*.md'ye gerekçesiyle kaydedilmelidir.
- Bu ADR, mevcut kodun (`candidate-profile`, `candidate-skills`) geriye dönük gözden geçirilmesini tetikledi (bkz. docs/CANDIDATE_SKILLS.md "Ek Düzeltme" bölümleri); benzer bir gözden geçirme, ileride her yeni modül/branch tamamlandığında da (ör. Job, Company) tekrarlanmalıdır.
