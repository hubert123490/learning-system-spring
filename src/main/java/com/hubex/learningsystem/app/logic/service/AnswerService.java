package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.requests.SubmitAnswersRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface AnswerService {
    UniversalResponse submitAnswers(String courseId, String examId, String submissionId, SubmitAnswersRequest request);
}
