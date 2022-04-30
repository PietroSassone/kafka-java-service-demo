package com.demo.acceptance.tests.stepdefs.product;

import java.util.Map;
import java.util.Objects;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.ProductDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.JsonHelper;
import com.demo.acceptance.tests.util.RequestUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;

public class CreateProductStepDefs extends BaseSteps {

    private static final String CREATE_PRODUCT_ENDPOINT_PATH = "/api/product/createProduct";
    private static final String REQUEST_TEMPLATE = "createProductRequestTemplate.json";
    private static final String PRODUCT_NAME_OUT_OF_BOUNDS_ERROR = "Product name must be between 3 and 50 characters!";
    private static final String PRICE_IS_TOO_BIG_ERROR = "Price can't be bigger than 100000000";
    private static final String PRICE_IS_NEGATIVE_ERROR = "Price can't be negative.";
    private static final String PRODUCT_NAME_OUT_OF_BOUNDS = "product name out of bounds";
    private static final String PRICE_TOO_BIG = "price too big";
    private static final String NEGATIVE_PRICE = "negative price";

    private static final Map<String, String> EXPECTED_ERRORS_MAP = Map.of(
        PRODUCT_NAME_OUT_OF_BOUNDS, PRODUCT_NAME_OUT_OF_BOUNDS_ERROR,
        PRICE_TOO_BIG, PRICE_IS_TOO_BIG_ERROR,
        NEGATIVE_PRICE, PRICE_IS_NEGATIVE_ERROR
    );

    private String name;
    private String price;
    private Response response;
    private ObjectNode request;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private FileReaderUtil fileReader;

    @Autowired
    private JsonHelper jsonHelper;

    @Autowired
    private ProductDao productDao;

    @Before("@createProduct")
    public void prepareTestData() {
        request = fileReader.readFileToJsonNode(REQUEST_TEMPLATE, TEST_DATA_FOLDER_PRODUCTS);
    }

    @After("@createProduct")
    public void afterTest() {
        final String productId = testDataRepository.getProductId();

        if (Objects.nonNull(productId)) {
            productDao.deleteResourceById(Long.parseLong(productId));
        }
    }

    @Given("^the productName value for the request is set to (.*)$")
    public void theProductNameIsSet(final String nameToSet) {
        name = keepStringOrSetToNull(nameToSet);
    }

    @Given("^the price value for the request is set to (.*)$")
    public void thePriceIsSet(final String priceToSet) {
        price = keepStringOrSetToNull(priceToSet);
    }

    @When("the createProduct endpoint is called")
    public void callTheCreateProductEndpoint() {
        request.put(PRODUCT_NAME_NODE_NAME, name);
        request.put(PRICE_NODE_NAME, price);

        response = requestUtil.executePostRequest(CREATE_PRODUCT_ENDPOINT_PATH, String.valueOf(request));
        testDataRepository.setResponse(response);
    }

    @Then("the response body should contain the new product")
    public void theResponseShouldContainTheNewProduct() throws JSONException {
        prepareCommonExpectedResponseJson();

        final ObjectNode expectedResponseJson = testDataRepository.getExpectedResponse();

        expectedResponseJson.put(PRODUCT_NAME_NODE_NAME, name);
        expectedResponseJson.put(PRICE_NODE_NAME, Double.valueOf(price));
        final ObjectNode actualResponseJson = response.readEntity(ObjectNode.class);

        testDataRepository.setProductId(actualResponseJson.get(ID_NODE_NAME).asText());

        assertNewResourceInResponseJson(expectedResponseJson, actualResponseJson);
    }

    @Then("^the response body should contain a (product name out of bounds|price too big|negative price) error")
    public void theResponseShouldContainAProductNameError(final String errorType) {
        assertErrorInResponse(EXPECTED_ERRORS_MAP.get(errorType));
    }
}
