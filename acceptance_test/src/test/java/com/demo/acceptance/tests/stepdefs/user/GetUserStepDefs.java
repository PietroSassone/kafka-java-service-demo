package com.demo.acceptance.tests.stepdefs.user;

import java.util.Objects;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.UserDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.DbTestDataManipulator;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.JsonHelper;
import com.demo.acceptance.tests.util.RequestUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetUserStepDefs extends BaseSteps {

    private static final String GET_USER_ENDPOINT_PATH = "/api/user/%s/getUser";
    private static final String TEST_DATA_FOLDER_USERS = "users";
    private static final String TEST_DATA_INSERT_FILENAME = "insertUserSql.sql";
    private static final String BALANCE_NODE_NAME = "moneyBalance";

    private String userName;
    private String formattedEndpointPath;
    private Response response;
    private Long existingUserId;
    private Double balance;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private FileReaderUtil fileReader;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private DbTestDataManipulator dbTestDataManipulator;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JsonHelper jsonHelper;

    @After("@getUser")
    public void afterTest() {
        if (Objects.nonNull(existingUserId)) {
            userDao.deleteResourceById(existingUserId);
        }
    }

    @Given("^a user exists in the database with name (.*) and a balance of (\\d+)$")
    public void aUserIsSavedInTheDatabase(final String userName, final double balance) {
        existingUserId = dbTestDataManipulator.insertIntoTableWithQueryParamsAndGetId(
            fileReader.readFileToString(TEST_DATA_INSERT_FILENAME, TEST_DATA_FOLDER_USERS),
            userName,
            balance
        );
        this.balance = balance;

        testDataRepository.setUserId(String.valueOf(existingUserId));
        testDataRepository.setUserName(userName);
        testDataRepository.setUserBalance(balance);
    }

    @And("^the username parameter for the request is set to (.*)$")
    public void theUserNameIsSet(final String userNameToSet) {
        userName = NULL_AS_STRING.equals(userNameToSet) ? null : userNameToSet;
        testDataRepository.setUserName(userName);
    }

    @When("the getUser endpoint is called")
    public void callTheGetUserEndpoint() {
        formattedEndpointPath = String.format(GET_USER_ENDPOINT_PATH, userName);
        response = requestUtil.executeGetRequest(formattedEndpointPath);
        testDataRepository.setResponse(response);
    }

    @Then("the response body should contain the correct user info")
    public void theResponseShouldContainTheExpectedUser() throws JSONException {
        final ObjectNode expectedResponseJson = fileReader.readFileToJsonNode(EXPECTED_RESPONSE_TEMPLATE, TEST_DATA_FOLDER_COMMON);

        expectedResponseJson.put(ID_NODE_NAME, existingUserId);
        expectedResponseJson.put(USER_NAME_NODE_NAME, userName);
        expectedResponseJson.put(BALANCE_NODE_NAME, balance);

        jsonHelper.setRestResponseLink(expectedResponseJson, formattedEndpointPath);

        JSONAssert.assertEquals(expectedResponseJson.toString(), response.readEntity(String.class), JSONCompareMode.LENIENT);
    }

    @Then("^the response body should contain a user not found by (id|userName) message$")
    public void theResponseShouldContainAUserNotFoundError(final String paramType) {
        final String expectedError = USER_NAME_NODE_NAME.equals(paramType)
            ? String.format("Could not find user with username: %s", testDataRepository.getUserName())
            : String.format("Could not find user with id: %s", testDataRepository.getUserId());

        assertErrorInResponse(expectedError);
    }
}
