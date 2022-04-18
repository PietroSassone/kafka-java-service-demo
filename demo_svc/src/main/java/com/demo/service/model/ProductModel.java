package com.demo.service.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
