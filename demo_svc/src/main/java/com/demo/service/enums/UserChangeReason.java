package com.demo.service.enums;

/**
 * Enum for the user state changes supported by the user controller.
 */
public enum UserChangeReason {
    USER_CREATED,
    USER_NAME_CHANGE,
    BALANCE_REDUCED,
    BALANCE_INCREASED,
    USER_DELETED
}
