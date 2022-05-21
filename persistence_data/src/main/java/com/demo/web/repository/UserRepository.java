package com.demo.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.demo.web.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Transactional
    Optional<UserEntity> findByUsername(String userName);
}
