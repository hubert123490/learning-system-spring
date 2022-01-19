package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.TaskSubmissionServiceImpl;
import com.hubex.learningsystem.app.models.dtos.TaskSubmissionDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/assignments/{assignmentId}/submissions")
public class TaskSubmissionController {
    private final TaskSubmissionServiceImpl taskSubmissionService;

    public TaskSubmissionController(TaskSubmissionServiceImpl taskSubmissionService) {
        this.taskSubmissionService = taskSubmissionService;
    }

    @GetMapping("/find-all-submissions")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<TaskSubmissionDTO> findAllSubmissions(@PathVariable String courseId, @PathVariable String assignmentId) {
        return taskSubmissionService.findAllSubmissions(courseId, assignmentId);
    }

    @GetMapping("/unchecked-submissions")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<TaskSubmissionDTO> findUncheckedTaskSubmissions(@PathVariable String courseId, @PathVariable String assignmentId) {
        return taskSubmissionService.findUncheckedTaskSubmissions(courseId, assignmentId);
    }
}
