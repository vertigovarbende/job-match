package com.deveyk.jobmatch.testsupport;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class TestContainerConfiguration extends LogTrackerConfiguration {

    // ------ POSTGRES ------
    private static final String POSTGRESQL_IMAGE = "postgres:16-alpine";
    // ------ KEYCLOAK ------
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.7.3";
    private static final String KEYCLOAK_REALM = "jobmatch";
    private static final String KEYCLOAK_REALM_IMPORT_CLASSPATH_FILE = "/keycloak/realm-export.json";

    @ServiceConnection
    protected static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(DockerImageName.parse(POSTGRESQL_IMAGE))
            .withDatabaseName("jobmatch-test-db")
            .withUsername("jobmatch-test")
            .withPassword("jobmatch-test")
            .withReuse(true);

    protected static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer(KEYCLOAK_IMAGE)
            .withRealmImportFile(KEYCLOAK_REALM_IMPORT_CLASSPATH_FILE)
            .withReuse(true);

    static {
        POSTGRESQL_CONTAINER.start();
        KEYCLOAK_CONTAINER.start();
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> KEYCLOAK_CONTAINER.getIssuerUrl(KEYCLOAK_REALM));
    }

}
