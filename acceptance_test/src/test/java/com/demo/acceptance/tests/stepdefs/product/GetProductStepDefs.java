package com.demo.acceptance.tests.stepdefs.product;

import java.util.Objects;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.ProductDao;
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

public class GetProductStepDefs extends BaseSteps {

    private static final String GET_PRODUCT_ENDPOINT_PATH = "/api/product/%s/getProduct";
    private static final String TEST_DATA_INSERT_FILENAME = "insertProductSql.sql";

    private String productId;
    private Long existingProductId;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private FileReaderUtil fileReader;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private DbTestDataManipulator dbTestDataManipulator;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private JsonHelper jsonHelper;

    @After("@getProduct")
    public void afterTest() {
        if (Objects.nonNull(existingProductId)) {
            productDao.deleteResourceById(existingProductId);
            existingProductId = null;
        }
    }

    @Given("^a product exists in the database with name (.*) and a price of (\\d+)$")
    public void aProductIsSavedInTheDatabase(final String productName, final double price) {
        existingProductId = dbTestDataManipulator.insertIntoTableWithQueryParamsAndGetId(
            fileReader.readFileToString(TEST_DATA_INSERT_FILENAME, TEST_DATA_FOLDER_PRODUCTS),
            productName,
            price
        );

        final String productId = String.valueOf(existingProductId);

        testDataRepository.setProductId(productId);
        testDataRepository.getProductIds().add(productId);
        testDataRepository.setProductName(productName);
        testDataRepository.setPrice(price);
    }

    @And("^the product id parameter for the request is set to (.*)$")
    public void theProductIdIsSet(final String idToSet) {
        productId = keepStringOrSetToNull(idToSet);
        testDataRepository.setProductId(productId);
    }

    @When("the getProduct endpoint is called")
    public void callTheGetProductEndpoint() {
        final String requestUrl = String.format(
            GET_PRODUCT_ENDPOINT_PATH,
            Objects.isNull(existingProductId) ? productId : existingProductId
        );

        testDataRepository.setResourceSelfLink(requestUrl);
        testDataRepository.setResponse(requestUtil.executeGetRequest(requestUrl));
    }

    @Then("the response body should contain the correct product")
    public void theResponseShouldContainTheExpectedProduct() throws JSONException {
        final ObjectNode expectedResponseJson = fileReader.readFileToJsonNode(EXPECTED_RESPONSE_TEMPLATE, TEST_DATA_FOLDER_COMMON);

        expectedResponseJson.put(ID_NODE_NAME, Long.valueOf(testDataRepository.getProductId()));
        expectedResponseJson.put(PRODUCT_NAME_NODE_NAME, testDataRepository.getProductName());
        expectedResponseJson.put(PRICE_NODE_NAME, testDataRepository.getPrice());

        jsonHelper.setRestResponseLink(expectedResponseJson, testDataRepository.getResourceSelfLink());

        JSONAssert.assertEquals(expectedResponseJson.toString(), testDataRepository.getResponse().readEntity(String.class), JSONCompareMode.LENIENT);
    }

    @Then("the response body should contain a product not found message")
    public void theResponseShouldContainAProductNotFoundError() {
        assertErrorInResponse(String.format("Could not find product with id: %s", testDataRepository.getProductId()));
    }
}
