package com.demo.service.service.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.demo.service.events.PurchaseEvent;
import com.demo.service.exception.ProductNotFoundException;
import com.demo.service.exception.UserNotFoundException;
import com.demo.service.service.ProductService;
import com.demo.service.service.PurchaseService;
import com.demo.service.service.UserService;
import com.demo.web.entity.ProductEntity;
import com.demo.web.entity.PurchaseEntity;
import com.demo.web.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaEventListener {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private final CountDownLatch purchaseEventLatch = new CountDownLatch(1);

    @KafkaListener(topics = "${purchase.topic.name}", containerFactory = "purchaseEventKafkaListenerContainerFactory")
    public void purchaseEventListener(final PurchaseEvent purchaseEvent) {
        log.info("Received purchaseEvent message: {}", purchaseEvent);
        this.purchaseEventLatch.countDown();

        final UserEntity userFromThePurchase = userService.findByUserId(purchaseEvent.getUserId())
            .orElseThrow(() -> new UserNotFoundException(purchaseEvent.getUserId()));

        final List<ProductEntity> productsFromThePurchase = new ArrayList<>();
        final Map<Long, Integer> productIdsWithQuantities = new HashMap<>();

        purchaseEvent.getPurchaseDetails().forEach(details -> {
            final Long productId = Long.parseLong(details.getProductId());
            productsFromThePurchase.add(
                productService.findByProductId(productId).orElseThrow(() -> new ProductNotFoundException(productId))
            );

            productIdsWithQuantities.put(productId, details.getQuantity());
        });

        final PurchaseEntity receivedPurchase = PurchaseEntity.builder()
            .id(purchaseEvent.getEventId())
            .user(userFromThePurchase)
            .productEntities(productsFromThePurchase)
            .productIdsWithQuantities(productIdsWithQuantities)
            .totalValue(purchaseEvent.getTotalValue())
            .build();

        purchaseService.savePurchase(receivedPurchase);
    }
}
