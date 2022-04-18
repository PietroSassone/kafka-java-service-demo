package com.demo.acceptance.tests.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DbCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void deleteUserById(final Long userId) {
        log.info("Deleting user by id {}", userId);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
    }

    @Transactional
    public void deleteUserByName(final String userName) {
        log.info("Deleting user by name {}", userName);
        jdbcTemplate.update("DELETE FROM users WHERE username = ?", userName);
    }

    @Transactional
    public void deleteProductId(final Long productId) {
        log.info("Deleting product by id {}", productId);
        jdbcTemplate.update("DELETE FROM products WHERE product_id = ?", productId);
    }

}
