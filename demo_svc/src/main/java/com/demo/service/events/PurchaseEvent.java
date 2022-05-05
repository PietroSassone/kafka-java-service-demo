package com.demo.service.events;

import java.util.List;

import com.demo.service.model.PurchaseDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PurchaseEvent {

    private long eventId;
    private long userId;
    private List<PurchaseDetail> purchaseDetails;
    private Double totalValue;
}
