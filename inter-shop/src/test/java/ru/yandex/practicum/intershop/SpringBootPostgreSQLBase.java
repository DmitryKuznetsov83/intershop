package ru.yandex.practicum.intershop;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class SpringBootPostgreSQLBase {

    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:17-alpine")
                .withDatabaseName("intershop_db")
                .withUsername("intershop_user")
                .withPassword("intershop_password")
                .withReuse(false);
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {

        String r2dbcUrl = String.format(
                "r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName()
        );
        // r2dbc
        registry.add("spring.r2dbc.url", () -> r2dbcUrl);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        // jdbc (liquibase)
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);

    }

}