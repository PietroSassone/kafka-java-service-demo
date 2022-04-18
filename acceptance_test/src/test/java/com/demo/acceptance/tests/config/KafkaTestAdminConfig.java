package com.demo.acceptance.tests.config;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTestAdminConfig {

    @Value(value = "${kafka.bootstrapServer:localhost:9092}")
    private String bootstrapServer;

    @Bean
    public AdminClient adminClient() {
        Properties kafkaAdminProperties = new Properties();
        kafkaAdminProperties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        return AdminClient.create(kafkaAdminProperties);
    }
}
