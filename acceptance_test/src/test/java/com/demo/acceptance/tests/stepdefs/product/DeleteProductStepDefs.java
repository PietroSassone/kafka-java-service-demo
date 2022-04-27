package com.demo.acceptance.tests.stepdefs.product;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.ProductDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.RequestUtil;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;

public class DeleteProductStepDefs extends BaseSteps {

    private static final String DELETE_PRODUCT_ENDPOINT_PATH = "/api/product/products/%s";

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private ProductDao productDao;

    @When("the deleteProduct endpoint is called")
    public void callTheDeleteProductEndpoint() {
        Response response = requestUtil.executeDeleteRequest(String.format(DELETE_PRODUCT_ENDPOINT_PATH, testDataRepository.getProductId()));
        testDataRepository.setResponse(response);
    }

    @Then("^the product should( not)? be present in the database$")
    public void theProductShouldOrShouldNotBePresentInTheDb(final String shouldNot) {
        final boolean shouldBePresent = StringUtils.isEmpty(shouldNot);

        final Long productId = Long.parseLong(testDataRepository.getProductId());

        assertThat(
            String.format("The product with id %s should be present in the products table: %s", productId, shouldBePresent),
            productDao.findResourceById(productId).isPresent(),
            equalTo(shouldBePresent)
        );
    }
}
