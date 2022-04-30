package com.demo.acceptance.tests.repository;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component
public class TestDataRepository {

    private Response response;
    private ObjectNode expectedResponse;
    private String productId;
    private String userId;
    private String userName;
    private Double userBalance;

    public void resetTestDataRepository() {
        log.info("Resetting saved test data after a test.");
        response = null;
        expectedResponse = null;
        productId = null;
        userId = null;
        userName = null;
        userBalance = null;
    }
}
