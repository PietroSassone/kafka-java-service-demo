package com.demo.acceptance.tests.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseSteps {
    private static final String LINKS_NODE_NAME = "_links";
    
    protected static final String NULL_AS_STRING = "null";
    protected static final String USER_NAME_NODE_NAME = "userName";
    protected static final String BALANCE_NODE_NAME = "moneyBalance";
    protected static final String ID_NODE_NAME = "id";
    protected static final String TEST_DATA_FOLDER_COMMON = "common";
    protected static final String PRODUCT_NAME_NODE_NAME = "productName";
    protected static final String PRICE_NODE_NAME = "price";
    protected static final String TEST_DATA_FOLDER_PRODUCTS = "products";
    protected static final String TEST_DATA_FOLDER_USERS = "users";
    protected static final String EXPECTED_RESPONSE_TEMPLATE = "getEndpointResponseTemplate.json";

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private FileReaderUtil fileReader;

    protected void assertErrorInResponse(final String expectedError) {
        assertThat(
            String.format("The response body should contain the error \"%s\"!", expectedError),
            testDataRepository.getResponse().readEntity(String.class),
            containsString(expectedError)
        );
    }

    protected String keepStringOrSetToNull(final String valueToSet) {
        return NULL_AS_STRING.equals(valueToSet) ? null : valueToSet;
    }

    protected void prepareCommonExpectedResponseJson() {
        testDataRepository.setExpectedResponse(fileReader.readFileToJsonNode(EXPECTED_RESPONSE_TEMPLATE, TEST_DATA_FOLDER_COMMON));
    }

    protected ObjectNode prepareUserRequestBody(final ObjectNode request) {
        request.put(USER_NAME_NODE_NAME, testDataRepository.getUserName());
        request.put(BALANCE_NODE_NAME, testDataRepository.getUserBalanceAsString());

        return request;
    }

    protected String prepareProductRequestBody(final ObjectNode request) {
        request.put(PRODUCT_NAME_NODE_NAME, testDataRepository.getProductName());
        request.put(PRICE_NODE_NAME, testDataRepository.getPriceAsString());

        return String.valueOf(request);
    }

    protected void assertNewResourceInResponseJson(final ObjectNode expectedResponseJson, final ObjectNode actualResponseJson) throws JSONException {
        final ValueMatcher<Object> jsonIgnoreMatcher = (firstJsonNode, secondJsonNode) -> true;

        JSONAssert.assertEquals(
            expectedResponseJson.toString(),
            actualResponseJson.toString(),
            new CustomComparator(
                JSONCompareMode.LENIENT,
                new Customization(ID_NODE_NAME, jsonIgnoreMatcher),
                new Customization(LINKS_NODE_NAME, jsonIgnoreMatcher)
            )
        );
    }
}
