package com.antkorwin.junit5integrationtestutils.test.runners;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 29.08.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@EnableTestsWithEmbeddedWebServer
public class EnableTestsWithEmbeddedWebServerTest {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private String port;

    @Test
    void testA() {
        // Arrange
        String url = "http://localhost:" + port + "/api/test";
        // Act
        String test = restTemplate.getForObject(url, String.class);
        // Assert
        assertThat(test).isEqualTo("A");
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }

        @RestController
        @RequestMapping("api")
        public class AController {

            @GetMapping("test")
            public String test() {
                return "A";
            }
        }
    }
}
