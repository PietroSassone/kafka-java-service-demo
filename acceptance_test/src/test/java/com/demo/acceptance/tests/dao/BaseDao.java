package com.demo.acceptance.tests.dao;

import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class BaseDao<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    @Transactional(readOnly = true)
    public Optional<T> findResourceById(final Long resourceId) {
        Optional<T> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(getSqlSelectQueryById(), rowMapper, Objects.requireNonNull(resourceId)));
        } catch (EmptyResultDataAccessException exception) {
            result = Optional.empty();
        }
        return result;
    }

    @Transactional
    public void deleteResourceById(final Long resourceId) {
        log.info("Deleting resource with id {}", Objects.requireNonNull(resourceId));
        jdbcTemplate.update(getSqlDeleteQueryById(), resourceId);
    }

    protected abstract String getSqlSelectQueryById();

    protected abstract String getSqlDeleteQueryById();
}
