package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.AssignmentDTO;
import com.hubex.learningsystem.app.models.requests.ChangeDatesRequest;
import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface AssignmentService {
    UniversalResponse createAssignment(CreateAssignmentRequest request, String courseId);
    UniversalResponse deleteAssignment(String courseId, String assignmentId);
    List<AssignmentDTO> getPendingAssignments();
    List<AssignmentDTO> getCourseAssignments(String courseId);
    List<AssignmentDTO> getUncheckedAssignments();
    UniversalResponse changeAssignmentDates(String courseId, String assignmentId, ChangeDatesRequest request);
}
