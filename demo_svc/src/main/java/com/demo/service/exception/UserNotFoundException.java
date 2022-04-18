package com.demo.service.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String userName) {
        super("Could not find user with username: " + userName);
    }

    public UserNotFoundException(final Long id) {
        super("Could not find user with id: " + id);
    }
}
