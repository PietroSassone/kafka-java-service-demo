package com.demo.web.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request model class for the requests expected bz the update user endpoint.
 */
@Getter
@ToString
@NoArgsConstructor
public class CreateUserRequest extends UserOperationRequest {

    public CreateUserRequest(final String userName, final double balance) {
        super(userName, balance);
    }
}
