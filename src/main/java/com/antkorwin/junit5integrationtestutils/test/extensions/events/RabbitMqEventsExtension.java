package com.antkorwin.junit5integrationtestutils.test.extensions.events;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 22.01.2019.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
public class RabbitMqEventsExtension implements BeforeAllCallback, AfterEachCallback {

    private AmqpTemplate amqpTemplate;

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        ExpectedEvent expectedEvent = context.getRequiredTestMethod()
                                             .getAnnotation(ExpectedEvent.class);

        Message message = amqpTemplate.receive(expectedEvent.queue(), expectedEvent.timeout());
        assertThat(new String(message.getBody())).isEqualTo(expectedEvent.message());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        amqpTemplate = SpringExtension.getApplicationContext(context)
                                      .getBean(AmqpTemplate.class);

        if (amqpTemplate == null) {
            throw new RuntimeException("Not found the AmqpTemplate bean in the current spring context.");
        }
    }
}
