package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseRequest {
    private String name;
    private String category;
    private String password;
}
