package com.demo.service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.service.PurchaseService;
import com.demo.web.entity.PurchaseEntity;
import com.demo.web.repository.PurchaseRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation of save operation for purchases.
 */
@Slf4j
@Component
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public PurchaseEntity savePurchase(final PurchaseEntity purchase) {
        log.info("Saving the purchase event content to the database.");
        return purchaseRepository.save(purchase);
    }
}
