package com.demo.acceptance.tests.stepdefs.product;

import java.util.Objects;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.ProductDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
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

public class GetProductStepdefs {

    private static final String NULL_AS_STRING = "null";
    private static final String GET_PRODUCT_ENDPOINT_PATH = "/api/product/%s/getProduct";
    private static final String TEST_DATA_FOLDER_COMMON = "common";
    private static final String TEST_DATA_FOLDER_PRODUCTS = "products";
    private static final String TEST_DATA_INSERT_FILENAME = "insertProductSql.sql";
    private static final String EXPECTED_RESPONSE_TEMPLATE = "getEndpointResponseTemplate.json";
    private static final String ID_NODE_NAME = "id";
    private static final String PRODUCT_NAME_NODE_NAME = "productName";
    private static final String PRICE_NODE_NAME = "price";

    private String productId;
    private String productName;
    private String formattedEndpointPath;
    private Response response;
    private Long existingProductId;
    private Double price;

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
        this.productName = productName;
        this.price = price;
    }

    @And("^the product id parameter for the request is set to (.*)$")
    public void theProductIdIsSet(final String idToSet) {
        productId = NULL_AS_STRING.equals(idToSet) ? null : idToSet;
        testDataRepository.setProductId(productId);
    }

    @When("the getProduct endpoint is called")
    public void callTheGetProductEndpoint() {
        formattedEndpointPath = String.format(
            GET_PRODUCT_ENDPOINT_PATH,
            Objects.isNull(existingProductId) ? productId : existingProductId
        );
        response = requestUtil.executeGetRequest(formattedEndpointPath);
        testDataRepository.setResponse(response);
    }

    @Then("the response body should contain the correct product")
    public void theResponseShouldContainTheExpectedProduct() throws JSONException {
        final ObjectNode expectedResponseJson = fileReader.readFileToJsonNode(EXPECTED_RESPONSE_TEMPLATE, TEST_DATA_FOLDER_COMMON);

        expectedResponseJson.put(ID_NODE_NAME, existingProductId);
        expectedResponseJson.put(PRODUCT_NAME_NODE_NAME, productName);
        expectedResponseJson.put(PRICE_NODE_NAME, price);

        jsonHelper.setRestResponseLink(expectedResponseJson, formattedEndpointPath);

        JSONAssert.assertEquals(expectedResponseJson.toString(), response.readEntity(String.class), JSONCompareMode.LENIENT);
    }

}
