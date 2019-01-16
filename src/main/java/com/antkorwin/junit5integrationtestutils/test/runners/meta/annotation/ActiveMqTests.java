package com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.antkorwin.junit5integrationtestutils.test.extensions.ActiveMqTcExtension;
import com.antkorwin.junit5integrationtestutils.test.extensions.RabbitMqTcExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created on 07.08.2018.
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Tag("rabbitmq-test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(ActiveMqTcExtension.class)
public @interface ActiveMqTests {
}
