package com.demo.web.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.demo.service.exception.ProductNotFoundException;

@ControllerAdvice
public class ProductControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected String productNotFoundHandler(final ProductNotFoundException exception) {
        return exception.getMessage();
    }
}
