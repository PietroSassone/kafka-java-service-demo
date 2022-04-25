package com.demo.acceptance.tests.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import io.cucumber.java.en.Then;

public class CommonStepdefs {

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "username";
    private static final String PRODUCT = "product";

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

    @Then("^the response body should contain a (product|user id|username) not found message$")
    public void theResponseShouldContainAResourceNotFoundError(final String resourceType) {
        final Map<String, String> expectedErrorsMap = Map.of(
            USER_ID, String.format("Could not find user with id: %s", testDataRepository.getUserId()),
            USER_NAME, String.format("Could not find user with username: %s", testDataRepository.getUserName()),
            PRODUCT, String.format("Could not find product with id: %s", testDataRepository.getProductId())
        );

        final String expectedError = expectedErrorsMap.get(resourceType);

        assertThat(
            String.format("The response body should contain the error \"%s\"!", expectedError),
            testDataRepository.getResponse().readEntity(String.class),
            containsString(expectedError)
        );
    }
}
