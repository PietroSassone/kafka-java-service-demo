package com.demo.service.events;

import java.util.Map;

import com.demo.web.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PurchaseEvent {

    private final long eventId;
    private final long userId;
    private final Map<ProductEntity, Integer> productsWithQuantities;
    private final Double totalValue;
}
