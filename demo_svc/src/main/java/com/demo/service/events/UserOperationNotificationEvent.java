package com.demo.service.events;

import com.demo.service.enums.UserChangeReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data stored about users sent in Kafka events.
 */
@Getter
@ToString
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserOperationNotificationEvent {

    @EqualsAndHashCode.Exclude
    private long eventId;

    private long userId;
    private String userName;
    private double balance;
    private UserChangeReason changeReason;
}
