package com.demo.service.model;

import com.demo.web.entity.ProductEntity;
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
    private ProductEntity product;
    private int quantity;
}
