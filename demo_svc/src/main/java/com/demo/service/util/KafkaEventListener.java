package com.demo.service.util;

import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.demo.service.events.PurchaseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaEventListener {

    private final CountDownLatch purchaseEventLatch = new CountDownLatch(1);

    @KafkaListener(topics = "${purchase.topic.name}", containerFactory = "purchaseEventKafkaListenerContainerFactory")
    public void purchaseEventListener(final PurchaseEvent purchaseEvent) {
        log.info("Received purchaseEvent message: {}", purchaseEvent);
        this.purchaseEventLatch.countDown();
    }
}
