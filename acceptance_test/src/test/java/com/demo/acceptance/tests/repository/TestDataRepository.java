package com.demo.acceptance.tests.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to pass data between different cucumber stepdefs.
 */
@Slf4j
@Getter
@Setter
@Component
public class TestDataRepository {

    private Response response;
    private ObjectNode expectedResponse;
    private ObjectNode requestAsJson;
    private String productId;
    private List<String> productIds = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();
    private String userId;
    private String userName;
    private Double userBalance;
    private String userBalanceAsString;
    private String productName;
    private Double price;
    private String priceAsString;
    private String resourceSelfLink;

    public void resetTestDataRepository() {
        log.info("Resetting saved test data after a test.");
        response = null;
        expectedResponse = null;
        requestAsJson = null;
        productId = null;
        productIds.clear();
        userIds.clear();
        userId = null;
        userName = null;
        userBalance = null;
        userBalanceAsString = null;
        productName = null;
        price = null;
        priceAsString = null;
        resourceSelfLink = null;
    }
}
