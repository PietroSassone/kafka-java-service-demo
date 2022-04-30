package com.demo.acceptance.tests.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.demo.service.config.KafkaProducerConfig;
import com.demo.service.events.PurchaseEvent;
import com.demo.service.events.UserOperationNotificationEvent;

@Configuration
@Import(KafkaProducerConfig.class)
public class KafkaTestConfig {

    private static final String PARTITION_BYTE_SIZE_CONFIG = "20971520";

    @Value(value = "${kafka.bootstrapServer:localhost:9092}")
    private String bootstrapAddress;

    @Value(value = "${user.topic.group.id:userNotificationEventGroup}")
    private String userNotifTopicGroupId;

    @Bean
    public ProducerFactory<Long, PurchaseEvent> purchaseEventProducerFactory() {
        final Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProperties);
    }

    @Bean
    public KafkaTemplate<Long, PurchaseEvent> purchaseEventKafkaTemplate() {
        return new KafkaTemplate<>(purchaseEventProducerFactory());
    }

    @Bean
    public KafkaConsumer<Long, UserOperationNotificationEvent> userEventKafkaConsumer() {
        return new KafkaConsumer<>(setKafkaConsumerProperties(), new LongDeserializer(), new JsonDeserializer<>(UserOperationNotificationEvent.class));
    }

    private Map<String, Object> setKafkaConsumerProperties() {
        final Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, userNotifTopicGroupId);
        consumerProperties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, PARTITION_BYTE_SIZE_CONFIG);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return consumerProperties;
    }
}
