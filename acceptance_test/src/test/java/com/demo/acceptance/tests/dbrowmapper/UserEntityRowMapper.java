package com.demo.acceptance.tests.dbrowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.demo.web.entity.UserEntity;

@Component
public class UserEntityRowMapper implements RowMapper<UserEntity> {

    @Override
    public UserEntity mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        return UserEntity.builder()
            .id(resultSet.getLong("user_id"))
            .username(resultSet.getString("username"))
            .balance(resultSet.getDouble("balance"))
            .build();
    }
}
