package com.antkorwin.junit5integrationtestutils.test.extensions.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 22.01.2019.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectedEvent {

    String message();

    String queue();

    int timeout() default 3000;
}
