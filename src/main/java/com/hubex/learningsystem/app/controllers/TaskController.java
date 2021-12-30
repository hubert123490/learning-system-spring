package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.TaskServiceImpl;
import com.hubex.learningsystem.app.models.dtos.TaskDTO;
import com.hubex.learningsystem.app.models.requests.CreateTaskRequest;
import com.hubex.learningsystem.app.models.requests.webex.CreateMeetingRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/assignments/{assignmentId}/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public List<TaskDTO> getAllTasks(@PathVariable String courseId, @PathVariable String assignmentId) {
        List<TaskDTO> response = taskService.getTasks(courseId, assignmentId);
        if (response.isEmpty()) {
            return new ArrayList<>();
        } else {
            return response;
        }
    }

    @PostMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addTask(@PathVariable String courseId, @PathVariable String assignmentId, @RequestBody CreateTaskRequest request) {
        return taskService.addTask(courseId, assignmentId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse deleteTask(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId) {
        return taskService.deleteTask(courseId, assignmentId, taskId);
    }

    @PatchMapping("/{taskId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse updateDescription(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId, @Valid @RequestBody String description) {
        return taskService.updateDescription(courseId, assignmentId, taskId, description);
    }

    @PostMapping("{taskId}/upload-file")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse uploadFileToTask(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId, @RequestParam("file") MultipartFile file) {
        return taskService.uploadFileToTask(courseId, assignmentId, taskId, file);
    }

    @DeleteMapping("{taskId}/{fileId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse deleteFileFromTask(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId, @PathVariable String fileId) {
        return taskService.deleteFileFromTask(courseId, assignmentId, taskId, fileId);
    }
}
