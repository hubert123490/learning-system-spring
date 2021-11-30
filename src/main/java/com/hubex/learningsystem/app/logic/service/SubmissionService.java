package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.SubmissionDTO;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface SubmissionService {
    UniversalResponse makeSubmission(String courseId, String examId);
    UniversalResponse checkSubmission(String courseId, String examId);
    List<SubmissionDTO> findSubmissions(String courseId, String examId);
}
