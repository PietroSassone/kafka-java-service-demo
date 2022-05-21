package com.demo.acceptance.tests.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Util class to to insert DB records and return the new record's primary key.
 */
@Slf4j
@Component
public class DbTestDataManipulator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertIntoTableWithQueryParamsAndGetId(final String sqlInsertQuery, final Object... queryParameters) {
        final List<Object> queryParamList = Arrays.asList(queryParameters);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
            dbConnection -> {
                final PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlInsertQuery, Statement.RETURN_GENERATED_KEYS);
                IntStream.range(0, queryParamList.size()).forEach(
                    listIndex -> {
                        try {
                            preparedStatement.setObject(listIndex + 1, queryParamList.get(listIndex));
                        } catch (SQLException exception) {
                            throw new RuntimeException(exception.getMessage());
                        }
                    });
                log.info("Inserting record with SQL: {}", preparedStatement);
                return preparedStatement;
            },
            keyHolder);

        final Long insertedResourceId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        log.info("Newly inserted record id: {}", insertedResourceId);
        return insertedResourceId;
    }
}
