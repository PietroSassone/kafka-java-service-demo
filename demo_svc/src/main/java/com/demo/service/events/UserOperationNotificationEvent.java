package com.demo.service.events;

import java.util.Random;

import com.demo.service.enums.UserChangeReason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class UserOperationNotificationEvent {
    private final long eventId;
    private final long userId;
    private final String userName;
    private final double balance;
    private final UserChangeReason changeReason;

    public UserOperationNotificationEvent(final long userId, final String userName, final double balance, final UserChangeReason changeReason) {
        this.eventId = new Random().nextLong();
        this.userId = userId;
        this.userName = userName;
        this.balance = balance;
        this.changeReason = changeReason;
    }
}
