package com.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.demo.service.config.ServiceConfig;
import com.demo.service.util.KafkaEventListener;

@SpringBootApplication
@EnableAutoConfiguration
@Import(ServiceConfig.class)
public class Application {

    @Autowired
    private KafkaEventListener kafkaEventListener;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
