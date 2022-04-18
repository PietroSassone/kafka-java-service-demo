package com.demo.acceptance.tests.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.demo.service.config.KafkaProducerConfig;

@Configuration
@Import(KafkaProducerConfig.class)
public class KafkaTestConfig {
}
