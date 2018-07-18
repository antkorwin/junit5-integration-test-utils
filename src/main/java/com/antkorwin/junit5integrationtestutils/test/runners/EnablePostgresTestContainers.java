package com.antkorwin.junit5integrationtestutils.test.runners;

import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 17.07.2018.
 *
 * Start a PostgreSQL container in Docker, by a test-containers library.
 *
 * @author Korovin Anatoliy
 */
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
        "spring.datasource.type=com.zaxxer.hikari.HikariDataSource",
        "spring.datasource.username=user",
        "spring.datasource.password=password",
        "spring.datasource.url=jdbc:tc:postgresql://hostname/databasename",
        "spring.jpa.generate-ddl=true",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
        // tracing:
        "spring.jpa.properties.hibernate.type=trace",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.use_sql_comments=true",
        "logging.level.org.hibernate.type.descriptor.sql=trace",
        // sql assert:
        "spring.jpa.properties.hibernate.session_factory.statement_inspector=com.antkorwin.junit5integrationtestutils.sqltracker.StatementInspectorImpl"
})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnablePostgresTestContainers {
}
