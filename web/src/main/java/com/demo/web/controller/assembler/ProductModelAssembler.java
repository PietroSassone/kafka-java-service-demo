package com.demo.web.controller.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.demo.service.model.ProductModel;
import com.demo.web.controller.impl.ProductController;
import com.demo.web.entity.ProductEntity;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<ProductEntity, ProductModel> {

    @Override
    public ProductModel toModel(final ProductEntity entity) {
        final ProductModel productModel = ProductModel.builder()
            .id(entity.getId())
            .productName(entity.getProductName())
            .price(entity.getPrice())
            .build();

        productModel.add(WebMvcLinkBuilder.linkTo(methodOn(ProductController.class).getProduct(entity.getId())).withSelfRel());

        return productModel;
    }

    @Override
    public CollectionModel<ProductModel> toCollectionModel(final Iterable<? extends ProductEntity> entities) {
        final CollectionModel<ProductModel> productEntities = RepresentationModelAssembler.super.toCollectionModel(entities);

        productEntities.add(WebMvcLinkBuilder.linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());

        return productEntities;
    }
}
