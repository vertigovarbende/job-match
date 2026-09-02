package com.deveyk.jobmatch.testsupport;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class TestContainerConfiguration extends LogTrackerConfiguration {

    private static final String POSTGRESQL_IMAGE = "postgres:16-alpine";

    @ServiceConnection
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRESQL_IMAGE))
            .withDatabaseName("jobmatch-test-db")
            .withUsername("jobmatch-test")
            .withPassword("jobmatch-test")
            .withReuse(true);

    static {
        POSTGRESQL_CONTAINER.start();
    }

}
