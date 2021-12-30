package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.TaskDTO;
import com.hubex.learningsystem.app.models.requests.CreateTaskRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getTasks(String courseId, String assignmentId);
    UniversalResponse addTask(String courseId, String assignmentId, CreateTaskRequest request);
    UniversalResponse deleteTask(String courseId, String assignmentId, String taskId);
    UniversalResponse updateDescription(String courseId, String assignmentId, String taskId, String description);
    UniversalResponse uploadFileToTask(String courseId, String assignmentId, String taskId, MultipartFile file);
    UniversalResponse deleteFileFromTask(String courseId, String assignmentId, String taskId, String fileId);
}
