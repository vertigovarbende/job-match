# Testing Strategy

## Test Pyramid

### Unit Tests

Domain logic hızlı unit testlerle kapsanmalıdır.

Örnek:

- SalaryRange validation
- Job publish state transition
- Duplicate application rule
- Matching score calculation

### Application Tests

Use-case servisleri mock/fake portlarla test edilir.

Örneğin:

```text
ApplyToJobUseCase
- candidate exists
- job published
- duplicate application absent
- save invoked
```

### Integration Tests

Testcontainers kullanılmalıdır:

- PostgreSQLContainer
- ElasticsearchContainer
- Kafka container
- Redis container

Önemli senaryolar:

1. JPA mapping
2. Flyway migration
3. Elasticsearch mapping/query
4. Kafka event consume/index
5. Security filters

### API Tests

MockMvc veya RestAssured ile:

- request validation
- authentication
- authorization
- HTTP status
- error contract

## Elasticsearch Testleri

Gerçek Elasticsearch container ile test edilmesi önerilir.

Mock Elasticsearch client, query DSL hatalarını yakalayamaz.

Senaryolar:

- exact title match higher score
- required skill match higher score
- filters do not affect relevance score unexpectedly
- typo tolerance
- pagination
- sorting

## Contract Tests

Contract testleri **Spring Cloud Contract** ile Groovy DSL kullanılarak yazılır (`.groovy` contract dosyaları).

- REST API contract'ları (bkz. API.md): her endpoint için request/response beklentisini tanımlayan `.groovy` contract dosyaları yazılır; plugin bunlardan otomatik test sınıfı ve consumer tarafı için stub (WireMock) üretir.
- Event/messaging contract'ları (bkz. EVENTS.md): Kafka producer'larının (`JobPublished`, `ApplicationSubmitted` vb.) ürettiği mesaj şeması `.groovy` contract'larla sabitlenir; consumer (search indexer gibi) bu stub'lara karşı test edilir.
- Contract'lar producer modülünün test kaynakları altında tutulur; `spring-cloud-contract-maven-plugin` build sırasında bunlardan test/stub üretir (`generateTestClasses` / `generateStubs`).
- Amaç: producer ile consumer arasındaki sözleşmenin (event şeması veya API şekli) sessizce kırılmasını engellemek — CI'da contract testi kırılırsa breaking change build'i durdurur.

Bu, event şeması stabilitesi için daha önce düşünülen JSON schema/Avro/Protobuf alternatifine tercih edilmiştir; JVM ekosisteminde tek bir DSL ile hem REST hem messaging contract'larını kapsaması artı bir noktadır.

## Paket Yapisi

Test kaynaklari feature-first paketlenir — ana koddaki (bkz. ARCHITECTURE.md #3) "controller/service/repository
seklinde yatay bolme yerine bounded context/feature bazli paketleme" prensibiyle tutarli olmasi icin, ust
seviyede test tipine gore degil feature'a gore bolunur; her feature paketinin icinde `unit`/`integration`/
`end2end` alt paketleri bulunur:

```text
com.deveyk.jobmatch
+-- architecture     -> cross-cutting, alt paket yok (bkz. ArchitectureTest)
+-- testsupport      -> cross-cutting, alt paket yok (LogTrackerConfiguration, TestContainerConfiguration vb.)
+-- shared
|   +-- unit
|   +-- integration
|   +-- end2end
+-- job              (ileride)
|   +-- unit
|   +-- integration
|   +-- end2end
+-- candidate        (ileride)
|   +-- unit
|   +-- integration
|   +-- end2end
+-- ...
```

`architecture` ve `testsupport`, ana koddaki `shared`in feature'lara kardes durmasi gibi, tek bir feature'a ait
olmadiklari icin `unit`/`integration`/`end2end`e bolunmez.

`unit`/`integration`/`end2end` altindaki paketler, ilgili feature'in kendi katman yapisini (`domain`,
`application`, `infrastructure`, `presentation`) birebir yansitir — ornegin
`shared.unit.presentation.exception.handler.GlobalExceptionHandlerTest`.

**Onemli kisitlar:**

- Bu paket yapisi yalnizca insan/IDE navigasyonu icindir; Maven'in Surefire/Failsafe ayrimi hala dosya adi
  sonekine gore calisir (`*Test.java` -> surefire/`mvn test`, `*IT.java`/`IT*.java`/`*ITCase.java` ->
  failsafe/`mvn verify`, bkz. asagidaki Build Tooling). Paket + sonek tutarli tutulmalidir — ornegin
  `integration` altindaki bir sinif `...IT.java` ile bitmelidir, yoksa sessizce yanlis build fazinda calisir.
- `unit`/`integration`/`end2end` alt paketi, test sinifinin package'ini test ettigi main sinifinkinden farkli
  kilar (ornegin `job.unit.domain` != `job.domain`). Bu, package-private (default-access) constructor/metod
  testlerini imkansiz hale getirir — Rich Domain Model'de (ADR-010) boyle bir constructor tercih edilirse bu
  paket yapisiyla ayni paketten test edilemez.
- Contract testleri (bkz. yukaridaki Contract Tests) icin `.groovy` dosyalari bu Java paket agacinda degil,
  Spring Cloud Contract'in konvansiyonu geregi `src/test/resources/contracts/<feature>/...` altinda tutulur;
  `end2end` paketleri yalnizca plugin'in urettigi testlerin extend edecegi base siniflari barindirir (plugin'in
  `basePackageForTests` ayari ilgili `<feature>.end2end` paketini hedef gosterecek sekilde yapilandirilir).

## Build Tooling

Unit ve integration testler build lifecycle'ında ayrılır:

```text
mvn test    → maven-surefire-plugin (unit/application tests, hızlı geri bildirim)
mvn verify  → + maven-failsafe-plugin (integration tests, Testcontainers)
```

Integration test sınıfları `*IT.java` naming convention'ı ile yazılır (ör. `JobRepositoryIT`, `SearchJobsApiIT`). Bu sınıflar `mvn test` sırasında çalışmaz; yalnızca `mvn verify` (failsafe `integration-test`/`verify` fazları) sırasında çalışır. Böylece unit test döngüsü hızlı kalır, Testcontainers gerektiren yavaş testler yalnızca `verify` adımında (bkz. Minimum Quality Gate) tetiklenir.

## Minimum Quality Gate

CI pipeline:

```text
compile
→ unit tests
→ integration tests
→ static analysis
→ package
```

Opsiyonel:

- JaCoCo
- SonarQube
- SpotBugs
- Checkstyle
