package com.demo.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.web.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
