package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface AssignmentService {
    UniversalResponse createAssignment(CreateAssignmentRequest request, String courseId);
    UniversalResponse deleteAssignment(String courseId, String assignmentId);
}
