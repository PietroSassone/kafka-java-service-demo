package com.demo.service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.demo.service.events.PurchaseEvent;

/**
 * Kafka consumer configuration for the service.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final String PARTITION_BYTE_SIZE_CONFIG = "20971520";

    @Value(value = "${kafka.bootstrapServer:localhost:9092}")
    private String bootstrapAddress;

    @Value(value = "${purchase.topic.group.id:purchaseEventGroup}")
    private String purchaseTopicGroupId;

    public ConsumerFactory<Long, PurchaseEvent> purchaseEventConsumerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, purchaseTopicGroupId);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, PARTITION_BYTE_SIZE_CONFIG);
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, PARTITION_BYTE_SIZE_CONFIG);
        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), new JsonDeserializer<>(PurchaseEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, PurchaseEvent> purchaseEventKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<Long, PurchaseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(purchaseEventConsumerFactory());
        return factory;
    }
}
