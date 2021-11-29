package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitAnswersRequest {
    private List<AnswerRequest> answers;
}
