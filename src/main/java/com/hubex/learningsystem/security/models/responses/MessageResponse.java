package com.hubex.learningsystem.security.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private String message;
    private String status;

    public MessageResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
