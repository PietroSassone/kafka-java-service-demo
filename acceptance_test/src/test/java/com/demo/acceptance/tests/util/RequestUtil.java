package com.demo.acceptance.tests.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Util class to store some common logic for executing different HTTP request.
 */
@Component
public class RequestUtil {

    @Autowired
    private WebTarget jerseyRestClient;

    public RequestUtil(WebTarget jerseyRestClient) {
        this.jerseyRestClient = jerseyRestClient;
    }

    public Response executeGetRequest(final String endpointPath) {
        return getJerseyClientBuilder(endpointPath).get();
    }

    public Response executeDeleteRequest(final String endpointPath) {
        return getJerseyClientBuilder(endpointPath).delete();
    }

    public Response executePostRequest(final String endpointPath, final String requestBody) {
        return getJerseyClientBuilder(endpointPath)
            .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE));
    }

    public Response executePutRequest(final String endpointPath, final String requestBody) {
        return getJerseyClientBuilder(endpointPath)
            .put(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE));
    }

    private Invocation.Builder getJerseyClientBuilder(final String endpointPath) {
        return jerseyRestClient.path(endpointPath).request().accept(MediaType.APPLICATION_JSON);
    }
}
