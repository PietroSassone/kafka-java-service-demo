package com.demo.web.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserOperationRequest {
    private static final int MINIMUM_PARAM_LENGTH = 6;
    private static final int MAXIMUM_PARAM_LENGTH = 20;
    private static final long MAXIMUM_BALANCE = 1000_000_000_000L;
    private static final String USERNAME_CONSTRAINTS_MESSAGE = "Username must be between 6 and 20 characters!";
    private static final String MIN_BALANCE_CONSTRAINT_MESSAGE = "User can't have negative balance.";
    private static final String MAX_BALANCE_CONSTRAINT_MESSAGE = "User can't have balance bigger than " + MAXIMUM_BALANCE;

    @NotBlank(message = USERNAME_CONSTRAINTS_MESSAGE)
    @Size(min = MINIMUM_PARAM_LENGTH, max = MAXIMUM_PARAM_LENGTH, message = USERNAME_CONSTRAINTS_MESSAGE)
    private String userName;

    @NotNull
    @Min(value = 0, message = MIN_BALANCE_CONSTRAINT_MESSAGE)
    @Max(value = MAXIMUM_BALANCE, message = MAX_BALANCE_CONSTRAINT_MESSAGE)
    private Double moneyBalance;
}
