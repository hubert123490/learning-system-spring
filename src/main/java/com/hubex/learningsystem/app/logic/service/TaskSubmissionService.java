package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.TaskSubmissionDTO;

import java.util.List;

public interface TaskSubmissionService {
    List<TaskSubmissionDTO> findAllSubmissions(String courseId, String assignmentId);
    List<TaskSubmissionDTO> findUncheckedTaskSubmissions(String courseId, String assignmentId);
}
