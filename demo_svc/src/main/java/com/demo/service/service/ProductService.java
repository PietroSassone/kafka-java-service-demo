package com.demo.service.service;

import java.util.List;
import java.util.Optional;

import com.demo.web.entity.ProductEntity;

public interface ProductService {

    ProductEntity saveProduct(ProductEntity product);

    Optional<ProductEntity> findByProductId(Long id);

    Optional<ProductEntity> findByProductName(String userName);

    List<ProductEntity> getAllProducts();

    void deleteById(Long id);
}
