package com.demo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Model class to store purchase details.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetail {
    private String productId;
    private String productName;
    private Double price;
    private int quantity;
}
