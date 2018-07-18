package com.antkorwin.junit5integrationtestutils.test.runners.stereotype;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableDataTests;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableRiderTests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 19.07.2018.
 *
 * Stereotype for the combination of DataTests & RiderTests
 *
 * @author Korovin Anatoliy
 */
@EnableDataTests
@EnableRiderTests
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface H2DataTests {
}
