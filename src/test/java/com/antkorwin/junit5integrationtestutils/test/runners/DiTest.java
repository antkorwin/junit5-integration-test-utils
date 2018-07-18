package com.antkorwin.junit5integrationtestutils.test.runners;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 18.07.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@EnableIntegrationTests
public class DiTest {

    @Autowired
    private TestConfig.TestBean testBean;


    @Test
    public void testDI() throws Exception {
        assertThat(testBean).isNotNull();
        assertThat(testBean.hello()).isEqualTo("hello world");
    }


    @TestConfiguration
    public static class TestConfig {

        @Component
        public class TestBean {
            public String hello() {
                return "hello world";
            }
        }
    }
}
