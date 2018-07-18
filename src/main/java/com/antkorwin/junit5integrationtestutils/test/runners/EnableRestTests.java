package com.antkorwin.junit5integrationtestutils.test.runners;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 17.07.2018.
 *
 * Enable configuration for tests with an API level.
 * Run entire application context and configure a MockMvc.
 *
 * Main test-cases:
 * - test REST API communication
 * - test API response/request converters
 * - test all application scoup from REST-API (for example: API->Service->DAO->JPA)
 *
 * @author Korovin Anatoliy
 */
@EnableIntegrationTests
@AutoConfigureMockMvc
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableRestTests {
}
