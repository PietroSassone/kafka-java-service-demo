package com.demo.acceptance.tests.dbrowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.demo.web.entity.PurchaseEntity;

/**
 * Row mapper for creating PurchaseEntity object from JDBC query result.
 */
@Component
public class PurchaseEntityRowMapper implements RowMapper<PurchaseEntity> {

    @Override
    public PurchaseEntity mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        return PurchaseEntity.builder()
            .id(resultSet.getLong("purchase_id"))
            .eventId(resultSet.getLong("event_id"))
            .totalValue(resultSet.getDouble("total_value"))
            .build();
    }
}
