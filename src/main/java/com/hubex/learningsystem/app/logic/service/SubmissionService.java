package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.SubmissionDTO;
import com.hubex.learningsystem.app.models.responses.CheckSubmissionResponse;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface SubmissionService {
    UniversalResponse makeSubmission(String courseId, String examId);
    CheckSubmissionResponse checkSubmission(String courseId, String examId);
    List<SubmissionDTO> findUncheckedSubmissions(String courseId, String examId);
    List<SubmissionDTO> findAllSubmissions(String courseId, String examId);
}
