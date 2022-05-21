package com.demo.acceptance.tests.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.demo.web.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO class for finding & deleting products in the database.
 */
@Slf4j
@Component
public class ProductDao extends BaseDao<ProductEntity> {

    private static final String PRODUCT_SELECT_QUERY = "SELECT * FROM products WHERE product_id = ?";
    private static final String PRODUCT_DELETE_QUERY = "DELETE FROM products WHERE product_id = ?";

    @Autowired
    public ProductDao(final JdbcTemplate jdbcTemplate, final RowMapper<ProductEntity> productEntityRowMapper) {
        super(jdbcTemplate, productEntityRowMapper);
    }

    @Override
    protected String getSqlSelectQueryById() {
        return PRODUCT_SELECT_QUERY;
    }

    @Override
    protected String getSqlDeleteQueryById() {
        log.info("Deleting product.");
        return PRODUCT_DELETE_QUERY;
    }
}
