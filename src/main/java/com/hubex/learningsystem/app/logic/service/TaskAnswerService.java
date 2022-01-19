package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.AnswerDTO;
import com.hubex.learningsystem.app.models.dtos.TaskAnswerDTO;
import com.hubex.learningsystem.app.models.requests.RateAnswerRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskAnswerService {
    UniversalResponse addTaskAnswerFile(String courseId, String assignmentId, String taskId, MultipartFile file);
    UniversalResponse deleteTaskAnswerFile(String courseId, String assignmentId, String taskId, String fileId);
    List<TaskAnswerDTO> getTaskSubmissionAnswers(String courseId, String assignmentId, String taskSubmissionId);
    UniversalResponse rateTaskAnswer(String courseId, String assignmentId, String taskSubmissionId, String taskAnswerId, RateAnswerRequest request);
    List<TaskAnswerDTO> getUncheckedTaskAnswers(String courseId, String assignmentId, String taskSubmissionId);
}
