package com.demo.service.events;

import java.util.List;

import com.demo.service.model.PurchaseDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data stored about purchases sent in Kafka events.
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PurchaseEvent {

    private Long eventId;
    private Long userId;
    private List<PurchaseDetail> purchaseDetails;
    private Double totalValue;
}
