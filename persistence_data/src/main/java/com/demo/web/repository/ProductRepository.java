package com.demo.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.demo.web.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Transactional
    Optional<ProductEntity> findByProductName(String productName);
}
