package com.demo.acceptance.tests.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.events.PurchaseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaEventDeserializer {

    private static final TypeReference<PurchaseEvent> EVENT_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Autowired
    private ObjectMapper objectMapper;

    public PurchaseEvent deserializeJsonToPurchaseEvent(final String eventJsonAsString) {
        PurchaseEvent deserializedEvent;
        try {
            deserializedEvent = objectMapper.readValue(eventJsonAsString, EVENT_TYPE_REFERENCE);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return deserializedEvent;
    }
}
