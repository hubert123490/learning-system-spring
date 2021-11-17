package com.hubex.learningsystem.app.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseResponse {
    private String message;
    private String status;

    public CreateCourseResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
