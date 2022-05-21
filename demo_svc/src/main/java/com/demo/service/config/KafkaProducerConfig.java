package com.demo.service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.demo.service.events.UserOperationNotificationEvent;

/**
 * Kafka producer configuration for the service.
 */
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrapServer:localhost:9092}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<Long, UserOperationNotificationEvent> userOperationNotificationProducerFactory() {
        final Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProperties);
    }

    @Bean
    public KafkaTemplate<Long, UserOperationNotificationEvent> userOperationNotificationKafkaTemplate() {
        return new KafkaTemplate<>(userOperationNotificationProducerFactory());
    }
}
