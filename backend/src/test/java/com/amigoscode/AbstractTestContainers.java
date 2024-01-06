package com.amigoscode;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestContainers {

    private static DataSource getDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());

        return builder.build(); // returns datasource
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            // initialize the postgress container, the test container sdk will create a container for us
            // as we finish the test the container will die at the end, after all the tests have been executed
            // this container is used for testing the dao layers
            new PostgreSQLContainer<>("postgres:latest") // creates a postgres container from this image ,  a container which has postgress running inside of it
                    .withDatabaseName("amigoscode-dao-unit-test")
                    .withUsername("amigoscode")
                    .withPassword("password"); // passed the postgress image

    // before all tests we want to migrate, becouse the container will be deleted
    @BeforeAll
    static void beforeAll() {
        // execute the migrations from the project path , resources/db.migration/migration1 on the postgress  test container with sql database on it (app)
        // apply flyway migratio automaticaly
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
    }


    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        // the registry object will allow us to register properties
        // modify properties to coonect to the test database from the test container
        registry.add(
                "spring.datasource.url",
                () -> postgreSQLContainer.getJdbcUrl()
        );

        registry.add(
                "spring.datasource.username",
                () -> postgreSQLContainer.getUsername()
        );

        registry.add(
                "spring.datasource.password",
                () -> postgreSQLContainer.getPassword()
        );

        // now as we run our test, it will map the specified properties from application.yml to the provided value coming from the container database
    }

}
