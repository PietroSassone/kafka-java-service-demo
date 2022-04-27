package com.demo.acceptance.tests.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;

public class BaseSteps {
    protected static final String NULL_AS_STRING = "null";

    @Autowired
    private TestDataRepository testDataRepository;

    protected void assertErrorInResponse(final String expectedError) {
        assertThat(
            String.format("The response body should contain the error \"%s\"!", expectedError),
            testDataRepository.getResponse().readEntity(String.class),
            containsString(expectedError)
        );
    }
}
