package com.demo.web.payload.request;

import javax.validation.constraints.NotNull;

import com.demo.service.enums.UserChangeReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UpdateUserRequest extends UserOperationRequest {

    @NotNull
    private UserChangeReason changeReason;

    public UpdateUserRequest(final String username, final double balance, final UserChangeReason changeReason) {
        super(username, balance);
        this.changeReason = changeReason;
    }
}
