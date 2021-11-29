package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionRequest {
    private String description;
    private String type;
    private String maxPoints;
}
