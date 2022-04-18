package com.demo.acceptance.tests;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.demo.acceptance.tests",
    plugin = {"pretty", "json:target/cucumber-report/cucumber.json"}
)
@CucumberContextConfiguration
public class RunCucumberTestsIT extends AbstractTestNGSpringContextTests {
}
