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
