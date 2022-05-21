package com.demo.acceptance.tests.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 * Spring config for the acceptance test module.
 */
@Configuration
@ComponentScan(basePackages = "com.demo.acceptance.tests")
@PropertySource("classpath:configuration/${CONFIG:acceptance-test-dev}.properties")
public class TestConfig {

    private static final String BASE_URL_TEMPLATE = "http://%s:%s";

    @Value("${serviceBaseUrl:localhost}")
    private String serviceBaseUrl;

    @Value("${servicePort:8080}")
    private int servicePort;

    @Value("${spring.datasource.driverClassName}")
    private String dbDriverClass;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    public WebTarget jerseyRestClient() {
        return ClientBuilder.newClient()
            .target(String.format(BASE_URL_TEMPLATE, serviceBaseUrl, servicePort))
            .register(new LoggingFeature(Logger.getLogger(TestConfig.class.getName()), Level.INFO, null, null));
    }

    @Bean
    public DataSource dbDataSource() {
        DriverManagerDataSource dbDataSource = new DriverManagerDataSource();
        dbDataSource.setDriverClassName(dbDriverClass);
        dbDataSource.setUrl(dbUrl);
        dbDataSource.setUsername(dbUsername);
        dbDataSource.setPassword(dbPassword);

        return dbDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dbDataSource) {
        return new JdbcTemplate(dbDataSource);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
