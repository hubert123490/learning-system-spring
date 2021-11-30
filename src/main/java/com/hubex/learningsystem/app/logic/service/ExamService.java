package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface ExamService {
    UniversalResponse createExam(CreateExamRequest request, String courseId);
    UniversalResponse deleteExam(String courseId, String examId);
}