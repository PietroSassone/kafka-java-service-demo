package com.demo.web.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CreateUserRequest extends UserOperationRequest {

    public CreateUserRequest(final String username, final double balance) {
        super(username, balance);
    }
}
