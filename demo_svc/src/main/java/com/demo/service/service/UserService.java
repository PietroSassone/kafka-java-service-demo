package com.demo.service.service;

import java.util.List;
import java.util.Optional;

import com.demo.web.entity.UserEntity;

public interface UserService {

    UserEntity saveUser(UserEntity user);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByUserId(Long id);

    List<UserEntity> getAllUsers();

    void deleteById(Long id);
}
