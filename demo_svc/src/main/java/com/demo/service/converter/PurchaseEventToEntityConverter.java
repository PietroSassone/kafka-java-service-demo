package com.demo.service.converter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.events.PurchaseEvent;
import com.demo.service.exception.ProductNotFoundException;
import com.demo.service.exception.UserNotFoundException;
import com.demo.service.model.PurchaseDetail;
import com.demo.service.service.ProductService;
import com.demo.service.service.UserService;
import com.demo.web.entity.ProductEntity;
import com.demo.web.entity.PurchaseEntity;
import com.demo.web.entity.UserEntity;

/**
 * Converter class to create purchase entities for persistence from purchase Kafka events.
 */
@Component
public class PurchaseEventToEntityConverter {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public PurchaseEntity convert(final PurchaseEvent purchaseEvent) {
        final UserEntity userFromThePurchase = getUserEntityOrException().apply(purchaseEvent);

        final List<ProductEntity> productsFromThePurchase = getProductsOrException().apply(getDetailStream().apply(purchaseEvent));
        final Map<Long, Integer> productIdsWithQuantities = getProductIdsWithQuantities(getDetailStream().apply(purchaseEvent));

        return PurchaseEntity.builder()
            .eventId(purchaseEvent.getEventId())
            .user(userFromThePurchase)
            .productEntities(productsFromThePurchase)
            .productIdsWithQuantities(productIdsWithQuantities)
            .totalValue(purchaseEvent.getTotalValue())
            .build();
    }

    private Function<PurchaseEvent, UserEntity> getUserEntityOrException() {
        return event -> userService.findByUserId(event.getUserId())
            .orElseThrow(() -> new UserNotFoundException(event.getUserId()));
    }

    private Function<PurchaseEvent, Stream<PurchaseDetail>> getDetailStream() {
        return event -> event.getPurchaseDetails().stream();
    }

    private Function<Stream<PurchaseDetail>, List<ProductEntity>> getProductsOrException() {
        return detailStream -> detailStream
            .map(PurchaseDetail::getProduct)
            .map(
                product -> productService.findByProductId(product.getId())
                    .orElseThrow(() -> new ProductNotFoundException(product.getId()))
            )
            .collect(Collectors.toList());
    }

    private Map<Long, Integer> getProductIdsWithQuantities(final Stream<PurchaseDetail> detailStream) {
        return detailStream.collect(Collectors.toMap(detail -> detail.getProduct().getId(), PurchaseDetail::getQuantity));
    }
}
