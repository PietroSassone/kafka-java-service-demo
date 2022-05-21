package com.demo.service.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.demo.service.events.PurchaseEvent;
import com.demo.service.exception.ProductNotFoundException;
import com.demo.service.exception.UserNotFoundException;
import com.demo.service.model.PurchaseDetail;
import com.demo.service.service.ProductService;
import com.demo.service.service.UserService;
import com.demo.web.entity.ProductEntity;
import com.demo.web.entity.PurchaseEntity;
import com.demo.web.entity.UserEntity;

public class PurchaseEventToEntityConverterTest {

    private static final String TEST_USER_NAME = "Mr Underhill";
    private static final Double TEST_USER_BALANCE = 2000.0;
    private static final Long TEST_USER_ID = 1L;
    private static final UserEntity TEST_USER = new UserEntity(TEST_USER_ID, TEST_USER_NAME, TEST_USER_BALANCE);

    private static final String TEST_PRODUCT_NAME = "silverware";
    private static final Double TEST_PRODUCT_PRICE = 1000.0;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final ProductEntity TEST_PRODUCT = new ProductEntity(TEST_PRODUCT_ID, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

    private static final String OTHER_TEST_PRODUCT_NAME = "tomato";
    private static final Double OTHER_TEST_PRODUCT_PRICE = 599.0;
    private static final Long OTHER_TEST_PRODUCT_ID = 123L;
    private static final ProductEntity OTHER_TEST_PRODUCT = new ProductEntity(OTHER_TEST_PRODUCT_ID, OTHER_TEST_PRODUCT_NAME, OTHER_TEST_PRODUCT_PRICE);

    private static final Long TEST_EVENT_ID = 1L;
    private static final Integer PRODUCT_QUANTITY = 1;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProductService productServiceMock;

    private PurchaseEventToEntityConverter undertest;

    @BeforeClass
    private void setupTest() {
        MockitoAnnotations.openMocks(this);

        undertest = new PurchaseEventToEntityConverter(userServiceMock, productServiceMock);
    }

    @Test(dataProvider = "productsForTestEventsDataProvider")
    public void testConvertShouldConvertEventWithAnyNumberProducts(final List<ProductEntity> productsInEvent) {
        // Given
        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.of(TEST_USER));
        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.of(TEST_PRODUCT));
        given(productServiceMock.findByProductId(OTHER_TEST_PRODUCT_ID)).willReturn(Optional.of(OTHER_TEST_PRODUCT));

        final PurchaseEvent testEvent = createTestEvent(TEST_EVENT_ID, TEST_USER_ID, productsInEvent);
        final PurchaseEntity expectedResult = createExpectedResult(productsInEvent);

        // When
        final PurchaseEntity actualResult = undertest.convert(testEvent);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test(dataProvider = "negativeInputDataProvider", expectedExceptions = IllegalArgumentException.class)
    public void testConvertShouldThrowExceptionOnInvalidEvent(final Long testEventId, final Long testUserId) {
        // Given
        final List<ProductEntity> productsInTestEvent = Collections.singletonList(TEST_PRODUCT);

        final PurchaseEvent testEvent = createTestEvent(testEventId, testUserId, productsInTestEvent);

        // When
        undertest.convert(testEvent);

        // Then - exception is thrown
    }

    @Test(expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find user with id: .*")
    public void testConvertShouldThrowExceptionOnNonexistentUser() {
        // Given
        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.empty());
        final List<ProductEntity> productsInTestEvent = List.of(TEST_PRODUCT, OTHER_TEST_PRODUCT);

        final PurchaseEvent testEvent = createTestEvent(TEST_EVENT_ID, TEST_USER_ID, productsInTestEvent);

        // When
        undertest.convert(testEvent);

        // Then - exception is thrown
    }

    @Test(expectedExceptions = ProductNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find product with id: .*")
    public void testConvertShouldThrowExceptionOnNonexistentProduct() {
        // Given
        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.of(TEST_USER));
        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.empty());
        final List<ProductEntity> productsInTestEvent = List.of(TEST_PRODUCT, OTHER_TEST_PRODUCT);

        final PurchaseEvent testEvent = createTestEvent(TEST_EVENT_ID, TEST_USER_ID, productsInTestEvent);

        // When
        undertest.convert(testEvent);

        // Then - exception is thrown
    }

    @DataProvider
    private Object[][] productsForTestEventsDataProvider() {
        return new Object[][]{
            {Collections.emptyList()},
            {Collections.singletonList(TEST_PRODUCT)},
            {List.of(TEST_PRODUCT, OTHER_TEST_PRODUCT)}
        };
    }

    @DataProvider
    private Object[][] negativeInputDataProvider() {
        return new Object[][]{
            {TEST_EVENT_ID, null},
            {null, TEST_USER_ID}
        };
    }

    private PurchaseEvent createTestEvent(final Long eventId, final Long userId, final List<ProductEntity> productsInEvent) {
        final List<PurchaseDetail> details = productsInEvent.stream()
            .map(productEntity -> new PurchaseDetail(productEntity, PRODUCT_QUANTITY))
            .collect(Collectors.toList());

        return PurchaseEvent.builder()
            .eventId(eventId)
            .userId(userId)
            .purchaseDetails(details)
            .totalValue(sumPrices(productsInEvent))
            .build();
    }

    private PurchaseEntity createExpectedResult(final List<ProductEntity> productsInEvent) {
        return PurchaseEntity.builder()
            .eventId(TEST_EVENT_ID)
            .user(TEST_USER)
            .productEntities(productsInEvent)
            .productIdsWithQuantities(getProductIdsWithQuantitiesMap(productsInEvent))
            .totalValue(sumPrices(productsInEvent))
            .build();
    }

    private Map<Long, Integer> getProductIdsWithQuantitiesMap(final List<ProductEntity> productsInEvent) {
        return productsInEvent.stream()
            .collect(Collectors.toMap(ProductEntity::getId, product -> PRODUCT_QUANTITY));
    }

    private Double sumPrices(final List<ProductEntity> productsInEvent) {
        return productsInEvent.stream()
            .map(ProductEntity::getPrice)
            .reduce(0.0, Double::sum);
    }
}