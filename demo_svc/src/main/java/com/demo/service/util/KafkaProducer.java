package com.demo.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.demo.service.events.UserOperationNotificationEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducer {

    @Autowired
    private final KafkaTemplate<String, UserOperationNotificationEvent> userOperationNotificationKafkaTemplate;

    public void sendUserChangeEvent(final String topicName, final UserOperationNotificationEvent event) {
        userOperationNotificationKafkaTemplate.send(topicName, event).addCallback(
            result -> log.info("Event sent to topic: {}", event),
            exception -> log.error("Failed to send event", exception)
        );
    }
}
