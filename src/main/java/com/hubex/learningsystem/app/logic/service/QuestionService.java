package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.QuestionDTO;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRadioRequest;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface QuestionService {
    List<QuestionDTO> getQuestions(String courseId, String examId);
    UniversalResponse addQuestion(String courseId, String examId, CreateQuestionRequest request);
    UniversalResponse deleteQuestion(String courseId, String examId, String questionId);
    UniversalResponse addQuestionRadio(String courseId, String examId, String questionId, CreateQuestionRadioRequest request);
    UniversalResponse addQuestionText(String courseId, String examId, String questionId, String correctAnswer);
    UniversalResponse addQuestionTextArea(String courseId, String examId, String questionId, String correctAnswer);
}
