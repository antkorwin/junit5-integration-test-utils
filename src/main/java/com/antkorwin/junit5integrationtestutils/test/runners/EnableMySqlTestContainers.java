package com.antkorwin.junit5integrationtestutils.test.runners;

import com.antkorwin.junit5integrationtestutils.test.extensions.MySqlTcExtension;
import com.antkorwin.junit5integrationtestutils.test.extensions.TraceSqlExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 18.07.2018.
 *
 * Start a MySQL container in Docker, by the test-containers library.
 *
 * @author Korovin Anatoliy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ContextConfiguration
@ExtendWith(MySqlTcExtension.class)
@ExtendWith(TraceSqlExtension.class)
@Tag("mysql")
public @interface EnableMySqlTestContainers {
}
