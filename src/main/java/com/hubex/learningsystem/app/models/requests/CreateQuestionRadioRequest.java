package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateQuestionRadioRequest {
    private String correctAnswer;
    List<String> queries;
}
