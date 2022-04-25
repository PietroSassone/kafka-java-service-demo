package com.demo.acceptance.tests.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
public class RequestUtil {

    @Autowired
    private WebTarget jerseyRestClient;

    public Response executeGetRequest(final String endpointPath) {
        return getJerseyClientBuilder(endpointPath).get();
    }

    public Response executeDeleteRequest(final String endpointPath) {
        return getJerseyClientBuilder(endpointPath).delete();
    }

    private Invocation.Builder getJerseyClientBuilder(final String endpointPath) {
        return jerseyRestClient.path(endpointPath).request().accept(MediaType.APPLICATION_JSON_TYPE);
    }
}
