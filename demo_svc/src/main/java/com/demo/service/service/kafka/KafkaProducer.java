package com.demo.service.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.demo.service.events.UserOperationNotificationEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Producer that sends events to Kafka on a given topic with given content.
 */
@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducer {

    @Autowired
    private final KafkaTemplate<Long, UserOperationNotificationEvent> userOperationNotificationKafkaTemplate;

    public void sendUserChangeEvent(final String topicName, final UserOperationNotificationEvent event) {
        userOperationNotificationKafkaTemplate.send(topicName, event).addCallback(
            result -> log.info("Event sent to topic: {}", event),
            exception -> log.error("Failed to send event", exception)
        );
    }
}
