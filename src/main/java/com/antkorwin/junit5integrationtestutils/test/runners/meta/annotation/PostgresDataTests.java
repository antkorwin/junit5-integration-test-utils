package com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableDataTests;
import com.antkorwin.junit5integrationtestutils.test.runners.EnablePostgresTestContainers;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableRiderTests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 19.07.2018.
 *
 * Stereotype for the combination of: DataTests, RiderTests
 * and PostgreSQL docker container.
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableDataTests
@EnableRiderTests
@EnablePostgresTestContainers
public @interface PostgresDataTests {
}
