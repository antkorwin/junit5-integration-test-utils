package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created on 17.08.2018.
 *
 * @author Korovin Anatoliy
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MeasureUnit {

    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
