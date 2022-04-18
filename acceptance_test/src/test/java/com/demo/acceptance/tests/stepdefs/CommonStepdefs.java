package com.demo.acceptance.tests.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import io.cucumber.java.en.Then;

public class CommonStepdefs {

    @Autowired
    private TestDataRepository testDataRepository;

    @Then("^the response status code should be (\\d+)$")
    public void theUserNameIsSet(final int expectedStatusCode) {
        assertThat(
            String.format("The response status code should be %s!", expectedStatusCode),
            testDataRepository.getResponse().getStatus(),
            equalTo(expectedStatusCode)
        );
    }
}
