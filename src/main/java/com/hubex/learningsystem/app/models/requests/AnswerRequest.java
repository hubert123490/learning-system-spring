package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
    private Long questionId;
    private String answer;
}
