package com.demo.service.events;

import java.util.Random;

import com.demo.service.enums.UserChangeReason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserOperationNotificationEvent {
    private long eventId;
    private long userId;
    private String userName;
    private double balance;
    private UserChangeReason changeReason;

    public UserOperationNotificationEvent(final long userId, final String userName, final double balance, final UserChangeReason changeReason) {
        this.eventId = new Random().nextLong();
        this.userId = userId;
        this.userName = userName;
        this.balance = balance;
        this.changeReason = changeReason;
    }
}
