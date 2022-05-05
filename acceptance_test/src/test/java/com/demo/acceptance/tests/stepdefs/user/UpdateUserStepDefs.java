package com.demo.acceptance.tests.stepdefs.user;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.RequestUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class UpdateUserStepDefs extends BaseSteps {
    private static final String UPDATE_USER_ENDPOINT_PATH = "/api/user/users/%s";
    private static final String CHANGE_REASON_NODE_NAME = "changeReason";
    private static final String SELF_LINK_TEMPLATE = "/api/user/%s/getUser";

    private String userChangeReason;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Given("^the change reason value for the request is set to (.*)$")
    public void theChangeReasonIsSet(final String reasonToSet) {
        userChangeReason = keepStringOrSetToNull(reasonToSet);
    }

    @When("^the updateUser endpoint is called(?: with the id of the existing user)?$")
    public void callTheUpdateUserEndpoint() {
        final ObjectNode request = prepareUserRequestBody(testDataRepository.getRequestAsJson());

        request.put(CHANGE_REASON_NODE_NAME, userChangeReason);
        testDataRepository.setResourceSelfLink(String.format(SELF_LINK_TEMPLATE, testDataRepository.getUserName()));

        testDataRepository.setResponse(
            requestUtil.executePutRequest(String.format(UPDATE_USER_ENDPOINT_PATH, testDataRepository.getUserId()), String.valueOf(request))
        );
    }
}
