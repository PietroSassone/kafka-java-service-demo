package com.demo.service.config;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Config to create Kafka topics for the service.
 */
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapServer:localhost:9092}")
    private String bootstrapServer;

    @Value(value = "${user.topic.name:user_operation_notification}")
    private String userNotificationTopicName;

    @Value(value = "${purchase.topic.name:purchase_notification}")
    private String purchaseNotificationTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> kafkaConfigProperties = new HashMap<>();
        kafkaConfigProperties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        return new KafkaAdmin(kafkaConfigProperties);
    }

    @Bean
    public NewTopic userNotificationTopic() {
        return new NewTopic(userNotificationTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic purchaseNotificationTopic() {
        return new NewTopic(purchaseNotificationTopicName, 1, (short) 1);
    }
}
