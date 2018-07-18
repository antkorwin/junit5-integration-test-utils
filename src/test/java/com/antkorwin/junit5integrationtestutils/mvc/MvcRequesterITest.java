package com.antkorwin.junit5integrationtestutils.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 16.07.2018.
 *
 * @author Korovin Anatoliy
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MvcRequesterITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void expectHttpStatus() throws Exception {
        // Act
        MvcRequester.on(mockMvc)
                    .to("/test/hello")
                    .get()
                    // Assert
                    .expectStatus(HttpStatus.OK);
    }

    @Test(expected = AssertionError.class)
    public void expectWithWrongStatusMustThrowAssertionError() throws Exception {
        // Act
        MvcRequester.on(mockMvc)
                    .to("/test/error")
                    .get()
                    // Assert
                    .expectStatus(HttpStatus.OK);
    }

    @Test
    public void expectStatusAndReturn() throws Exception {
        // Act
        String result = MvcRequester.on(mockMvc)
                                    .to("/test/hello")
                                    .get()
                                    // Assert
                                    .expectStatus(HttpStatus.OK)
                                    .returnAsPrimitive(String.class);

        assertThat(result).isEqualTo("hello world");
    }

    @Test
    public void returnAsPrimitiveToInteger() throws Exception {
        // Act
        Integer result = MvcRequester.on(mockMvc)
                                    .to("/test/integer")
                                    .get()
                                    // Assert
                                    .expectStatus(HttpStatus.OK)
                                    .returnAsPrimitive(Integer.class);

        assertThat(result).isEqualTo(42);
    }


    @TestConfiguration
    public static class TestConfig {

        @RestController
        @RequestMapping("/test")
        public class TestController {

            @GetMapping("/hello")
            public String hello() {
                return "hello world";
            }

            @GetMapping("/error")
            @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            public void error() {

            }

            @GetMapping("/integer")
            public int integer() {
                return 42;
            }
        }
    }
}