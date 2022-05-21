package com.demo.acceptance.tests.stepdefs.purchase;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.dao.ProductDao;
import com.demo.acceptance.tests.dao.PurchaseDao;
import com.demo.acceptance.tests.dao.UserDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.service.exception.PurchaseNotFoundException;
import com.demo.web.entity.PurchaseEntity;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;

public class PurchaseStepDefs extends BaseSteps {
    private static final Long TEST_EVENT_ID = 1L;
    private static final Double TEST_TOTAL_VALUE = 1000D;

    private Long purchaseId;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @After("@purchase")
    public void afterTest() {

        if (Objects.nonNull(purchaseId)) {
            purchaseDao.deleteAllPurchaseJoinTablesData(purchaseId);
            purchaseDao.deleteResourceById(TEST_EVENT_ID);
        }

        testDataRepository.getUserIds().forEach(userId -> userDao.deleteResourceById(Long.parseLong(userId)));
        testDataRepository.getProductIds().forEach(productId -> productDao.deleteResourceById(Long.parseLong(productId)));
    }

    @Then("^the purchase should( not)? be present in the database$")
    public void thePurchaseShouldOrShouldNotBePresentInTheDb(final String shouldNot) {
        final boolean shouldBePresent = StringUtils.isEmpty(shouldNot);

        await().atMost(TEN_SECONDS)
            .until(() -> purchaseDao.findResourceById(TEST_EVENT_ID).isPresent(), is(shouldBePresent));

        if (shouldBePresent) {
            final PurchaseEntity purchaseEntity = purchaseDao.findResourceById(TEST_EVENT_ID).orElseThrow(() -> new PurchaseNotFoundException(TEST_EVENT_ID));

            purchaseId = purchaseEntity.getId();

            assertThat("The event id of the saved purchase should be correct!", purchaseEntity.getEventId(), equalTo(TEST_EVENT_ID));
            assertThat("The total value of the saved purchase should be correct!", purchaseEntity.getTotalValue(), equalTo(TEST_TOTAL_VALUE));
        }
    }

}
