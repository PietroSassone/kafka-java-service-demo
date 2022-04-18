package com.demo.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.web.entity.PurchaseEntity;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

}
