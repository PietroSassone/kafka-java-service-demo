package com.demo.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuration class for the Swagger documentation added to the service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") final String openApiVersion) {
        return new OpenAPI().info(
            new Info()
                .title("Demo REST API with Kafka integration")
                .version(openApiVersion)
                .description("This is a demo REST service that has Kafka message consuming and producing integrated.")
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
