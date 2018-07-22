package com.antkorwin.junit5integrationtestutils.test.abstracts;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableIntegrationTests;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;

/**
 * Created on 22.07.2018.
 *
 * Abstract class to write an integration test with a RabbitMQ
 * faster than make it manually without an inheritance.
 *
 * This class uses the TestContainers library to start a docker image of the RabbitMQ.
 *
 * @author Korovin Anatoliy
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public abstract class BaseRabbitTest {

    private static final Integer RABBIT_PORT = 5672;
    private static GenericContainer rabbitmq =
            new GenericContainer("rabbitmq:management").withExposedPorts(RABBIT_PORT, 15672);

    static {
        rabbitmq.start();
        System.setProperty("spring.rabbitmq.host", rabbitmq.getContainerIpAddress());
        System.setProperty("spring.rabbitmq.port", rabbitmq.getMappedPort(RABBIT_PORT).toString());
    }
}
