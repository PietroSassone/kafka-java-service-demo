package com.demo.acceptance.tests.stepdefs.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.UserDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.RequestUtil;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;

public class DeleteUserStepDefs extends BaseSteps {

    private static final String DELETE_USER_ENDPOINT_PATH = "/api/user/users/%s";

    private Long userId;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private UserDao userDao;

    @After("@deleteUser")
    public void afterTest() {
        userId = null;
    }

    @And("^the user id parameter for the request is set to (.*)$")
    public void theUserIdIsSet(final String userIdToSet) {
        final String userIdString = NULL_AS_STRING.equals(userIdToSet) ? null : userIdToSet;
        testDataRepository.setUserId(userIdString);

        if (NumberUtils.isCreatable(userIdString)) {
            userId = Long.parseLong(userIdString);
        }
    }

    @When("the deleteUser endpoint is called")
    public void callTheDeleteUserEndpoint() {
        Response response = requestUtil.executeDeleteRequest(String.format(DELETE_USER_ENDPOINT_PATH, testDataRepository.getUserId()));
        testDataRepository.setResponse(response);
    }

    @Then("^the user should( not)? be present in the database$")
    public void theUserShouldOrShouldNotBePresentInTheDb(final String shouldNot) {
        final boolean shouldBePresent = StringUtils.isEmpty(shouldNot);

        if (Objects.isNull(userId)) {
            userId = Long.parseLong(testDataRepository.getUserId());
        }

        assertThat(
            String.format("The user with id %s should be present in the users table: %s", userId, shouldBePresent),
            userDao.findResourceById(userId).isPresent(),
            equalTo(shouldBePresent)
        );
    }
}
