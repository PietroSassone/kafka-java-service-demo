package com.demo.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring config for the service module.
 */
@Configuration
@ComponentScan(basePackages = "com.demo.service")
public class ServiceConfig {
}
