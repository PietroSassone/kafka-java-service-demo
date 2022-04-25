package com.demo.acceptance.tests.repository;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class TestDataRepository {

    private Response response;
    private String productId;
    private Long userId;
    private String userName;

    public void resetTestDataRepository() {
        response = null;
        productId = null;
        userId = null;
        userName = null;
    }
}
