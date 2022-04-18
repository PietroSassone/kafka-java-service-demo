package com.demo.service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.service.service.UserService;
import com.demo.web.entity.UserEntity;
import com.demo.web.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity saveUser(final UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByUserName(final String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public Optional<UserEntity> findByUserId(final Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(final Long id) {
        userRepository.deleteById(id);
    }
}
