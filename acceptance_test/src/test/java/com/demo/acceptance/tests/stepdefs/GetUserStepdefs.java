package com.demo.acceptance.tests.stepdefs;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.util.DbCleaner;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class GetUserStepdefs {

    private static final String NULL_AS_STRING = "null";
    private static final String GET_USER_ENDPOINT_PATH = "/%s/getUser";

    private String userName;
    private Response response;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private WebTarget jerseyRestClient;

    @Autowired
    private TestDataRepository testDataRepository;

    @After
    public void afterTest() {
        dbCleaner.deleteUserByName(userName);
    }

    @And("^I set the username parameter for the request to (.*)$")
    public void theUserNameIsSet(final String userNameToSet) {
        userName = NULL_AS_STRING.equals(userNameToSet) ? null : userNameToSet;
    }

    @When("I call the getUser endpoint")
    public void callTheGetUserEndpoint() {
        response = jerseyRestClient.path(String.format(GET_USER_ENDPOINT_PATH, userName))
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get();

        testDataRepository.setResponse(response);
    }

}
