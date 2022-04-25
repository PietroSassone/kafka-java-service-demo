package com.demo.acceptance.tests.dbrowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.demo.web.entity.ProductEntity;

@Component
public class ProductEntityRowMapper implements RowMapper<ProductEntity> {

    @Override
    public ProductEntity mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        return ProductEntity.builder()
            .id(resultSet.getLong("product_id"))
            .productName(resultSet.getString("productname"))
            .price(resultSet.getDouble("price"))
            .build();
    }
}
