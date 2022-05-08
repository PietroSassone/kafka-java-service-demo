package com.demo.service.exception;

/**
 * Custom exceptions for when a purchase is not found in the database.
 */
public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(final Long id) {
        super("Could not find purchase with id: " + id);
    }
}
