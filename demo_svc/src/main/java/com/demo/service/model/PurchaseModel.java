package com.demo.service.model;

import java.util.Map;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class PurchaseModel extends RepresentationModel<PurchaseModel> {

    private Long id;
    private Long userId;
    private Map<Long, Integer> productIdsWithQuantities;
    private Double totalValue;
}
