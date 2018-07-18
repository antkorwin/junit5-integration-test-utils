package com.antkorwin.junit5integrationtestutils.test.runners;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 17.07.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class VintageTest {

    @Autowired
    private TestConfig.TestBean testBean;


    @Test
    public void testDI() throws Exception {
        // Arrange
        // Act
        assertThat(testBean).isNotNull();
        // Assert
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
