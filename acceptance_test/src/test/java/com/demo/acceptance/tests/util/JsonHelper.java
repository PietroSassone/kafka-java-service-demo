package com.demo.acceptance.tests.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Util class to store some common logic for JSON node changes.
 */
@Component
public class JsonHelper {

    private static final String LINKS_NODE = "_links";
    private static final String SELF_NODE = "self";
    private static final String HREF_NODE = "href";
    private static final String SELF_LINK_TEMPLATE_STRING = "http://%s:%s%s";

    @Value("${serviceBaseUrl:localhost}")
    private String serviceBaseUrl;

    @Value("${servicePort:8080}")
    private int servicePort;

    public void setRestResponseLink(final ObjectNode jsonResponse, final String expectedSelfUrl) {
        ((ObjectNode) jsonResponse.get(LINKS_NODE).get(SELF_NODE)).put(HREF_NODE, String.format(SELF_LINK_TEMPLATE_STRING, serviceBaseUrl, servicePort, expectedSelfUrl));
    }

}
