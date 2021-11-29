package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface SubmissionService {
    UniversalResponse makeSubmission(String courseId, String examId);
    UniversalResponse checkSubmission(String courseId, String examId);
}
