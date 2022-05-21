package com.demo.service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.service.service.ProductService;
import com.demo.web.entity.ProductEntity;
import com.demo.web.repository.ProductRepository;

/**
 * Service implementation of CRUD operations for products.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductEntity saveProduct(final ProductEntity product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<ProductEntity> findByProductId(final Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<ProductEntity> findByProductName(final String productName) {
        return productRepository.findByProductName(productName);
    }

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteById(final Long id) {
        productRepository.deleteById(id);
    }
}
