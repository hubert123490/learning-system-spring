package com.hubex.learningsystem.app.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseResponse {
    private String message;
    private String status;
    private long id;

    public CreateCourseResponse(String message, String status, Long id) {
        this.message = message;
        this.status = status;
        this.id = id;
    }
}
