package com.antkorwin.junit5integrationtestutils.test.runners;

import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 18.07.2018.
 *
 * Configuration of the H2 database.
 *
 * @author Korovin Anatoliy
 */
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
public @interface EnableH2 {
}
