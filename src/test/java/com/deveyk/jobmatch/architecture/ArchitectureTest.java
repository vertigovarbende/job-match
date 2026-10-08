package com.deveyk.jobmatch.architecture;

import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * docs/ARCHITECTURE.md'de tanimlanan Clean Architecture kurallarini otomatik olarak denetler.
 * <p>
 * Kurallar, somut bir feature paketine (job, candidate, ...) degil, genel "..domain..",
 * "..application..", "..infrastructure..", "..presentation.." paket adlandirma konvansiyonuna
 * gore yazilir (bkz. ARCHITECTURE.md #3). Bu sayede kurallar bugun yalnizca "shared" modulunde
 * (shared.domain, shared.presentation) gecerliyken, ileride job/candidate/... feature'lari
 * eklendikce otomatik olarak onlari da kapsar — testi guncellemeye gerek kalmaz.
 */
@AnalyzeClasses(packages = "com.deveyk.jobmatch", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    /**
     * Bagimlilik yonu (bkz. ARCHITECTURE.md #2, #4):
     * Presentation -> Application -> Domain; Infrastructure de Application/Domain'e (port implementasyonu
     * icin) baglanabilir. Hicbir katman kendisinden "asagida" olmayan bir katman tarafindan erisilemez —
     * ozellikle Domain, framework/altyapi detaylarindan (Infrastructure) veya HTTP detaylarindan
     * (Presentation) habersiz kalmalidir.
     */
    @ArchTest
    static final ArchRule layers_should_respect_the_clean_architecture_dependency_rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Presentation").definedBy("..presentation..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Presentation", "Application", "Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    /**
     * Domain katmaninda @Entity, JpaRepository, ElasticsearchRepository, RestController, KafkaTemplate,
     * RedisTemplate bulunmaz (bkz. ARCHITECTURE.md #2). Domain, hicbir Spring/persistence/messaging
     * framework'unu bilmemelidir — Rich Domain Model saf business logic icerir (bkz. ADR-010).
     */
    @ArchTest
    static final ArchRule domain_should_not_depend_on_frameworks = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..",
                    "org.springframework.data.jpa..",
                    "org.springframework.data.elasticsearch..",
                    "org.springframework.data.redis..",
                    "org.springframework.kafka..",
                    "org.springframework.web.bind.annotation..",
                    "org.springframework.stereotype.."
            )
            .because("Domain katmani framework bagimliliklarindan izole tutulur (bkz. ARCHITECTURE.md #2, ADR-010).");

    /**
     * Ust seviye paketler (shared, job, candidate, ...) arasinda dongusel bagimlilik olusmasini engeller
     * (bkz. ARCHITECTURE.md #7 — "Modulerin monolith sinirlari").
     */
    @ArchTest
    static final ArchRule top_level_packages_should_be_free_of_cycles = slices()
            .matching("com.deveyk.jobmatch.(*)..")
            .should().beFreeOfCycles();

    /**
     * Domain entity/aggregate siniflarinda (JmBaseDomain'den tureyenler) setter bulunmaz; mutasyon yalnizca
     * anlamli domain metodlari uzerinden yapilir (bkz. ADR-010, ADR-012 Consequences). Lombok'un @Setter'i da
     * derlenmis bytecode'da setX metodu urettigi icin bu kural onu da yakalar. Kural bilerek JmBaseDomain
     * ailesiyle sinirlidir: domain.model altindaki JmPage/JmSort gibi sayfalama tipleri entity degildir
     * (JmPageable istek DTO'su JmSort'u extend eder ve setter'lara ihtiyac duyabilir).
     */
    @ArchTest
    static final ArchRule domain_models_should_not_expose_setters = noMethods()
            .that().areDeclaredInClassesThat().areAssignableTo(JmBaseDomain.class)
            .should().haveNameMatching("set[A-Z].*")
            .because("Domain modelinde setter yoktur, mutasyon domain metodlariyla yapilir (bkz. ADR-010, ADR-012).");

    /**
     * Repository port'lari (persistence soyutlamalari) tum modullerde ayni konumda durur:
     * "..application.port.out..". Domain katmaninda ya da baska bir pakette port arayuzu tanimlanmaz
     * (bkz. REFACTOR_02 madde 6). Infrastructure altindaki Spring Data arayuzleri (SpringData*JpaRepository,
     * *ElasticsearchRepository) port degil, framework arayuzleridir; kural onlari kapsamaz.
     */
    @ArchTest
    static final ArchRule repository_ports_should_reside_in_application_port_out = classes()
            .that().areInterfaces()
            .and().haveSimpleNameEndingWith("Repository")
            .and().resideOutsideOfPackage("..infrastructure..")
            .should().resideInAPackage("..application.port.out..")
            .because("Port'lar butun modullerde application/port/out altinda toplanir (bkz. REFACTOR_02 madde 6).");

    /**
     * Persistence (Postgres) adapter'lari Elasticsearch adapter'larina bagimli olmaz: her teknoloji kendi
     * paketinde yasar ve ikisi yalnizca application katmanindaki port'lar uzerinden birlestirilir
     * (bkz. REFACTOR_02 madde 7).
     */
    @ArchTest
    static final ArchRule persistence_should_not_depend_on_elasticsearch = noClasses()
            .that().resideInAPackage("..infrastructure.persistence..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.elasticsearch..")
            .because("Postgres ve Elasticsearch adapter'lari birbirini bilmez; yalnizca port'lar uzerinden konusulur (bkz. REFACTOR_02 madde 7).");

}
