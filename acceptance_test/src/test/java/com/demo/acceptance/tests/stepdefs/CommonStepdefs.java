package com.demo.acceptance.tests.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.util.FileReaderUtil;
import io.cucumber.java.en.Then;

public class CommonStepdefs extends BaseSteps {

    private static final String PARAMETER_IS_NULL = "parameter is null";
    private static final String WRONG_REQUEST = "wrong request";
    private static final String RESOURCE_ALREADY_EXISTS = "resource already exists";

    private static final String MUST_NOT_BE_NULL_ERROR = "must not be null";
    private static final String INCORRECT_HTTP_REQUEST_ERROR = "Incorrect HTTP request was received.";
    private static final String RESOURCE_EXISTS_ERROR = "The resource already exists in the database.";

    private static final Map<String, String> EXPECTED_ERRORS_MAP = Map.of(
        PARAMETER_IS_NULL, MUST_NOT_BE_NULL_ERROR,
        WRONG_REQUEST, INCORRECT_HTTP_REQUEST_ERROR,
        RESOURCE_ALREADY_EXISTS, RESOURCE_EXISTS_ERROR
    );

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private FileReaderUtil fileReader;

    @Then("^the response status code should be (\\d+)$")
    public void theUserNameIsSet(final int expectedStatusCode) {
        assertThat(
            String.format("The response status code should be %s!", expectedStatusCode),
            testDataRepository.getResponse().getStatus(),
            equalTo(expectedStatusCode)
        );
    }

    @Then("the response body code should be empty")
    public void theResponseBodyShouldBeEmpty() {
        assertThat("The response body be empty!!", testDataRepository.getResponse().readEntity(String.class), emptyString());
    }

    @Then("^the response body should contain a (resource already exists|parameter is null|wrong request) error$")
    public void theResponseShouldContainAnError(final String commonErrorType) {
        assertErrorInResponse(EXPECTED_ERRORS_MAP.get(commonErrorType));
    }
}
