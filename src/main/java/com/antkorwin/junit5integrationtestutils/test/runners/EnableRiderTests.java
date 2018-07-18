package com.antkorwin.junit5integrationtestutils.test.runners;

import com.github.database.rider.spring.api.DBRider;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 17.07.2018.
 *
 * Enable a configuration for the writing tests with
 * DatabaseRider library.
 *
 * You can combine this configuration with
 * EnableIntegrationTests  or  EnableDataTests.
 *
 * In order to work with a real DataBase in tests, you can use
 * this annotation in combination with some of TestContainersConfiguration:
 * - EnablePostgresTestContainers
 * - EnableMySqlTestContainers
 * By default use a configuration with H2
 *
 *
 * Main test-cases:
 * - init DataSet
 * - check expected DataSets after test
 * - generate DataSet
 *
 * @author Korovin Anatoliy
 */
@DBRider
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        // tracing:
        "spring.jpa.properties.hibernate.type=trace",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.use_sql_comments=true",
        "spring.jpa.properties.hibernate.connection.autocommit=false",
        "logging.level.org.hibernate.type.descriptor.sql=trace",
        // sql assert:
        "spring.jpa.properties.hibernate.session_factory.statement_inspector=com.antkorwin.junit5integrationtestutils.sqltracker.StatementInspectorImpl"
})
public @interface EnableRiderTests {
}
