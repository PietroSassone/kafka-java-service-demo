package com.demo.service.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Model class to return product details fetched from the database.
 */
@Data
@Getter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ProductModel extends RepresentationModel<ProductModel> {

    private Long id;
    private String productName;
    private Double price;
}
