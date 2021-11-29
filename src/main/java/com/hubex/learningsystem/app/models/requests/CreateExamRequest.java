package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateExamRequest {
    private String name;
    private String description;
}
