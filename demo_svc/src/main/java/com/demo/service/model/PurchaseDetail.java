package com.demo.service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PurchaseDetail {
    private String productId;
    private String productName;
    private Double price;
    private int quantity;
}
