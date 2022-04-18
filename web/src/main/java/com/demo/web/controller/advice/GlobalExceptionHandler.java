package com.demo.web.controller.advice;

import java.util.Map;

import javax.servlet.RequestDispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MESSAGE = "message";

    @Autowired
    private DefaultErrorAttributes defaultErrorAttributes;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(final RuntimeException exception, final WebRequest webRequest) {
        return createErrorResponse(exception, webRequest, HttpStatus.NOT_ACCEPTABLE, "The resource already exists in the database.");
    }

    @ExceptionHandler(InstantiationException.class)
    public ResponseEntity<Object> handleKafkaProducerException(final RuntimeException exception, final WebRequest webRequest) {
        return createErrorResponse(exception, webRequest, HttpStatus.EXPECTATION_FAILED, "There was an error with the Kafka producer.");
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception, final HttpHeaders headers, final HttpStatus status, final WebRequest webRequest) {
        return createErrorResponse(exception, webRequest, HttpStatus.BAD_REQUEST, exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }

    private ResponseEntity<Object> createErrorResponse(final Exception exception, final WebRequest webRequest, final HttpStatus responseStatus, final String responseMessage) {
        webRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, responseStatus.value(), RequestAttributes.SCOPE_REQUEST);
        final Map<String, Object> errorAttributes = defaultErrorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        errorAttributes.put(MESSAGE, responseMessage);
        return handleExceptionInternal(exception, errorAttributes, new HttpHeaders(), responseStatus, webRequest);
    }
}
