package com.demo.web.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductRequest {
    private static final int MINIMUM_PARAM_LENGTH = 3;
    private static final int MAXIMUM_PARAM_LENGTH = 50;
    private static final String PRODUCT_NAME_CONSTRAINTS_MESSAGE = "Product name must be between 3 and 50 characters!";

    @NotBlank(message = PRODUCT_NAME_CONSTRAINTS_MESSAGE)
    @Size(min = MINIMUM_PARAM_LENGTH, max = MAXIMUM_PARAM_LENGTH, message = PRODUCT_NAME_CONSTRAINTS_MESSAGE)
    private String productName;

    @Min(value = 0, message = "Price can't be negative.")
    @Max(value = Long.MAX_VALUE, message = "Price can't be bigger than " + Long.MAX_VALUE)
    private Double price;
}
