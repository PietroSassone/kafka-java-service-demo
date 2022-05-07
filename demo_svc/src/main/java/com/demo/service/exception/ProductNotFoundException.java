package com.demo.service.exception;

/**
 * Custom exceptions for when a product is not found in the database.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final Long id) {
        super("Could not find product with id: " + id);
    }
}
