package com.demo.acceptance.tests.stepdefs.product;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.RequestUtil;
import io.cucumber.java.en.When;

/**
 * Step definitions for the update product endpoint cucumber tests.
 */
public class UpdateProductStepDefs extends BaseSteps {
    private static final String UPDATE_PRODUCT_ENDPOINT_PATH = "/api/product/products/%s";
    private static final String SELF_LINK_TEMPLATE = "/api/product/%s/getProduct";

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @When("^the updateProduct endpoint is called(?: with the id of the existing product)?$")
    public void callTheUpdateProductEndpoint() {
        final String productId = testDataRepository.getProductId();

        testDataRepository.setResourceSelfLink(String.format(SELF_LINK_TEMPLATE, productId));

        testDataRepository.setResponse(
            requestUtil.executePutRequest(String.format(UPDATE_PRODUCT_ENDPOINT_PATH, productId), prepareProductRequestBody(testDataRepository.getRequestAsJson()))
        );
    }
}
