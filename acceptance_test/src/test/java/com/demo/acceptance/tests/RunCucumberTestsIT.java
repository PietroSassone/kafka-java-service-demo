package com.demo.acceptance.tests;

import org.springframework.test.context.ContextConfiguration;

import com.demo.acceptance.tests.config.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.demo.acceptance.tests",
    plugin = {"pretty", "json:target/cucumber-report/cucumber.json"}
)
@CucumberContextConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class RunCucumberTestsIT extends AbstractTestNGCucumberTests {
}
