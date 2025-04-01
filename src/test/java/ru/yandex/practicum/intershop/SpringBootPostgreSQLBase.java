package ru.yandex.practicum.intershop;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class SpringBootPostgreSQLBase {

    private static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:17-alpine")
                .withDatabaseName("intershop_db")
                .withUsername("intershop_user")
                .withPassword("intershop_password");
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

}