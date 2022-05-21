package com.demo.acceptance.tests.dao;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.demo.web.entity.PurchaseEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO class for finding & deleting purchases in the database.
 */
@Slf4j
@Component
public class PurchaseDao extends BaseDao<PurchaseEntity> {

    private static final String PURCHASE_SELECT_QUERY_BY_EVENT_ID = "SELECT * FROM purchases WHERE event_id = ?";
    private static final String PURCHASE_DELETE_QUERY = "DELETE FROM purchases WHERE event_id = ?";
    private static final String PURCHASE_USER_DATA_DELETE_QUERY = "DELETE FROM PURCHASE_FOR_USER WHERE purchase_id = ?";
    private static final String PURCHASE_PRODUCT_DATA_DELETE_QUERY = "DELETE FROM PURCHASES_WITH_PRODUCTS WHERE purchase_id = ?";
    private static final String PURCHASE_QUANTITY_DATA_DELETE_QUERY = "DELETE FROM PURCHASE_ENTITY_PRODUCT_IDS_WITH_QUANTITIES WHERE purchase_entity_purchase_id = ?";

    @Autowired
    public PurchaseDao(final JdbcTemplate jdbcTemplate, final RowMapper<PurchaseEntity> purchaseEntityRowMapper) {
        super(jdbcTemplate, purchaseEntityRowMapper);
    }

    @Transactional
    public void deleteAllPurchaseJoinTablesData(final Long resourceId) {
        log.info("Deleting purchase joined tables records with id {}", Objects.requireNonNull(resourceId));
        jdbcTemplate.update(PURCHASE_USER_DATA_DELETE_QUERY, resourceId);
        jdbcTemplate.update(PURCHASE_PRODUCT_DATA_DELETE_QUERY, resourceId);
        jdbcTemplate.update(PURCHASE_QUANTITY_DATA_DELETE_QUERY, resourceId);
    }

    @Override
    protected String getSqlSelectQueryById() {
        return PURCHASE_SELECT_QUERY_BY_EVENT_ID;
    }

    @Override
    protected String getSqlDeleteQueryById() {
        log.info("Deleting purchase data.");
        return PURCHASE_DELETE_QUERY;
    }
}
