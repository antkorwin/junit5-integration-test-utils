package com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableIntegrationTests;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableMySqlTestContainers;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableRiderTests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 19.07.2018.
 *
 * Stereotype for the combination of:
 * IntegrationTests, RiderTests and MySQL docker-container
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableIntegrationTests
@EnableRiderTests
@EnableMySqlTestContainers
public @interface MySqlIntegrationTests {
}
