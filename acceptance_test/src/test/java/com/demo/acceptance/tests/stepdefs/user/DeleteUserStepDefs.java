package com.demo.acceptance.tests.stepdefs.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.UserDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.RequestUtil;
import com.demo.web.entity.UserEntity;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;

public class DeleteUserStepDefs extends BaseSteps {

    private static final String DELETE_USER_ENDPOINT_PATH = "/api/user/users/%s";

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private UserDao userDao;

    @And("^the user id parameter for the request is set to (.*)$")
    public void theUserIdIsSet(final String userIdToSet) {
        final String userIdString = keepStringOrSetToNull(userIdToSet);
        testDataRepository.setUserId(userIdString);
    }

    @When("the deleteUser endpoint is called")
    public void callTheDeleteUserEndpoint() {
        Response response = requestUtil.executeDeleteRequest(String.format(DELETE_USER_ENDPOINT_PATH, testDataRepository.getUserId()));
        testDataRepository.setResponse(response);
    }

    @Then("^the user should( not)? be present in the database( with the updated details)?$")
    public void theUserShouldOrShouldNotBePresentInTheDb(final String shouldNot, final String checkDetails) {
        final boolean shouldBePresent = StringUtils.isEmpty(shouldNot);

        final Long userId = Long.parseLong(testDataRepository.getUserId());
        final Optional<UserEntity> optionalUser = userDao.findResourceById(userId);

        assertThat(
            String.format("The user with id %s should be present in the users table: %s", userId, shouldBePresent),
            optionalUser.isPresent(),
            equalTo(shouldBePresent)
        );

        if (StringUtils.isNotBlank(checkDetails)) {
            //The Optional.get() should not result in NPE, as the previous assert already checked the isPresent.
            final UserEntity userFromDb = optionalUser.get();
            assertThat("The name of the saved user should be correct!", userFromDb.getUsername(), equalTo(testDataRepository.getUserName()));
            assertThat("The balance of the saved user should be correct!", userFromDb.getBalance(), equalTo(testDataRepository.getUserBalance()));
        }
    }
}
