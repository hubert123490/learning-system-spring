package com.hubex.learningsystem.app.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniversalResponse {
    private String message;
    private String status;

    public UniversalResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
