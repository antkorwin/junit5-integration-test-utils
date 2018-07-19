package com.antkorwin.junit5integrationtestutils.test.runners;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 18.07.2018.
 * <p>
 * Configuration of the H2 database.
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ContextConfiguration
@TestPropertySource(locations = "classpath:antkorwin-h2.properties")
@Tag("h2")
public @interface EnableH2 {
}
