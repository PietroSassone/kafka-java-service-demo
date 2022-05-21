package com.demo.acceptance.tests.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.demo.web.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO class for finding & deleting users in the database.
 */
@Slf4j
@Component
public class UserDao extends BaseDao<UserEntity> {

    private static final String USER_SELECT_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String USER_DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate, final RowMapper<UserEntity> userEntityRowMapper) {
        super(jdbcTemplate, userEntityRowMapper);
    }

    @Override
    protected String getSqlSelectQueryById() {
        return USER_SELECT_QUERY;
    }

    @Override
    protected String getSqlDeleteQueryById() {
        log.info("Deleting user.");
        return USER_DELETE_QUERY;
    }
}
