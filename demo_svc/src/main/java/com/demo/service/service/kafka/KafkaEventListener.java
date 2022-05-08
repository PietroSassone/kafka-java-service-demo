package com.demo.service.service.kafka;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.demo.service.converter.PurchaseEventToEntityConverter;
import com.demo.service.events.PurchaseEvent;
import com.demo.service.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener that consumes events from Kafka on a given topic.
 */
@Slf4j
@Component
public class KafkaEventListener {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseEventToEntityConverter converter;

    private final CountDownLatch purchaseEventLatch = new CountDownLatch(1);

    @KafkaListener(topics = "${purchase.topic.name}", containerFactory = "purchaseEventKafkaListenerContainerFactory")
    public void purchaseEventListener(final PurchaseEvent purchaseEvent) {
        log.info("Received purchaseEvent message: {}", purchaseEvent);
        this.purchaseEventLatch.countDown();

        purchaseService.savePurchase(converter.convert(purchaseEvent));
    }
}
