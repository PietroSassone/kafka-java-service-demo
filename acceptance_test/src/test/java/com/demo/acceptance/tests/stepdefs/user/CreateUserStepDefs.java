package com.demo.acceptance.tests.stepdefs.user;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.UserDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.RequestUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the create user endpoint cucumber tests.
 */
public class CreateUserStepDefs extends BaseSteps {
    private static final String CREATE_USER_ENDPOINT_PATH = "/api/user/createUser";
    private static final String REQUEST_TEMPLATE = "createUserRequestTemplate.json";

    private static final String USER_NAME_OUT_OF_BOUNDS_ERROR = "Username must be between 6 and 20 characters!";
    private static final String BALANCE_IS_TOO_BIG_ERROR = "User can't have balance bigger than 1000000000000";
    private static final String BALANCE_IS_NEGATIVE_ERROR = "User can't have negative balance.";

    private static final String USER_NAME_OUT_OF_BOUNDS = "user name out of bounds";
    private static final String BALANCE_TOO_BIG = "balance too big";
    private static final String NEGATIVE_BALANCE = "negative balance";

    private static final Map<String, String> EXPECTED_ERRORS_MAP = Map.of(
        USER_NAME_OUT_OF_BOUNDS, USER_NAME_OUT_OF_BOUNDS_ERROR,
        BALANCE_TOO_BIG, BALANCE_IS_TOO_BIG_ERROR,
        NEGATIVE_BALANCE, BALANCE_IS_NEGATIVE_ERROR
    );

    private ObjectNode request;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private FileReaderUtil fileReader;

    @Autowired
    private UserDao userDao;

    @After("@createUser")
    public void afterTest() {
        testDataRepository.getUserIds().forEach(userId -> userDao.deleteResourceById(Long.parseLong(userId)));
    }

    @Given("a user request is created")
    public void aUserRequestIsCreated() {
        request = fileReader.readFileToJsonNode(REQUEST_TEMPLATE, TEST_DATA_FOLDER_USERS);
        testDataRepository.setRequestAsJson(request);
    }

    @Given("^the userName value for the request is set to (.*)$")
    public void theUserNameIsSet(final String nameToSet) {
        testDataRepository.setUserName(keepStringOrSetToNull(nameToSet));
    }

    @Given("^the balance value for the request is set to (.*)$")
    public void theBalanceIsSet(final String balanceToSet) {
        final String balance = keepStringOrSetToNull(balanceToSet);
        testDataRepository.setUserBalanceAsString(balance);

        if (NumberUtils.isCreatable(balance)) {
            testDataRepository.setUserBalance(Double.valueOf(balance));
        }
    }

    @When("the createUser endpoint is called")
    public void callTheCreateUserEndpoint() {
        testDataRepository.setResponse(requestUtil.executePostRequest(CREATE_USER_ENDPOINT_PATH, String.valueOf(prepareUserRequestBody(request))));
    }

    @Then("the response body should contain the new user")
    public void theResponseShouldContainTheNewUser() throws JSONException {
        prepareCommonExpectedResponseJson();

        final ObjectNode expectedResponseJson = testDataRepository.getExpectedResponse();
        expectedResponseJson.put(USER_NAME_NODE_NAME, testDataRepository.getUserName());
        expectedResponseJson.put(BALANCE_NODE_NAME, Double.valueOf(testDataRepository.getUserBalanceAsString()));

        final ObjectNode actualResponseJson = testDataRepository.getResponse().readEntity(ObjectNode.class);
        final String createdUserId = actualResponseJson.get(ID_NODE_NAME).asText();

        testDataRepository.setUserId(createdUserId);
        testDataRepository.getUserIds().add(createdUserId);

        assertNewResourceInResponseJson(expectedResponseJson, actualResponseJson);
    }

    @Then("^the response body should contain a (user name out of bounds|balance too big|negative balance) error")
    public void theResponseShouldContainAUserControllerError(final String errorType) {
        assertErrorInResponse(EXPECTED_ERRORS_MAP.get(errorType));
    }
}
