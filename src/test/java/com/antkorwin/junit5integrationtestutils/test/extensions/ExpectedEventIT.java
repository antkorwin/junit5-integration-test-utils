package com.antkorwin.junit5integrationtestutils.test.extensions;

import com.antkorwin.junit5integrationtestutils.test.extensions.events.ExpectedEvent;
import com.antkorwin.junit5integrationtestutils.test.extensions.events.RabbitMqEventsExtension;
import com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation.EnableRabbitMqTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Created on 22.01.2019.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@EnableRabbitMqTests
@ExtendWith(RabbitMqEventsExtension.class)
public class ExpectedEventIT {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    @ExpectedEvent(queue = "test-queue", message = "123")
    void testSend() throws InterruptedException {
        amqpTemplate.convertAndSend("test-queue", "123");
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public Queue testQueue() {
            return new Queue("test-queue");
        }
    }
}
