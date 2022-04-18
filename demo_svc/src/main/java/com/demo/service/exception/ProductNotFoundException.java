package com.demo.service.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final Long id) {
        super("Could not find product with id: " + id);
    }
}
