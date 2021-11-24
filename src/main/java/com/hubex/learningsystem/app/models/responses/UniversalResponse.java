package com.hubex.learningsystem.app.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UniversalResponse {
    private String message;
    private String status;
    private Long id = null;

    public UniversalResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
