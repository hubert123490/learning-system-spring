package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.AnswerDTO;
import com.hubex.learningsystem.app.models.requests.RateAnswerRequest;
import com.hubex.learningsystem.app.models.requests.SubmitAnswersRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface AnswerService {
    UniversalResponse submitAnswers(String courseId, String examId, String submissionId, SubmitAnswersRequest request);
    List<AnswerDTO> getUncheckedAnswers(String courseId, String examId, String submissionId);
    UniversalResponse rateAnswer(String courseId, String examId, String submissionId, String answerId, RateAnswerRequest request);
    List<AnswerDTO> getSubmissionAnswers(String courseId, String examId, String submissionId);
}
