package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.TaskAnswerServiceImpl;
import com.hubex.learningsystem.app.models.dtos.TaskAnswerDTO;
import com.hubex.learningsystem.app.models.requests.RateAnswerRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/assignments/{assignmentId}")
public class TaskAnswerController {
    private final TaskAnswerServiceImpl taskAnswerService;

    public TaskAnswerController(TaskAnswerServiceImpl taskAnswerService) {
        this.taskAnswerService = taskAnswerService;
    }

    @PostMapping("/tasks/{taskId}/task-answers/upload-file")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public UniversalResponse addTaskAnswerFile(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId, @RequestParam("file") MultipartFile file) {
        return taskAnswerService.addTaskAnswerFile(courseId, assignmentId, taskId, file);
    }

    @DeleteMapping("/tasks/{taskId}/task-answers/files/{fileId}")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public UniversalResponse deleteTaskAnswerFile(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskId,  @PathVariable String fileId) {
        return taskAnswerService.deleteTaskAnswerFile(courseId, assignmentId, taskId, fileId);
    }

    @GetMapping("/submissions/{taskSubmissionId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<TaskAnswerDTO> getTaskSubmissionAnswers(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskSubmissionId) {
        return taskAnswerService.getTaskSubmissionAnswers(courseId, assignmentId, taskSubmissionId);
    }

    @PatchMapping("/submissions/{taskSubmissionId}/task-answers/{taskAnswerId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse rateTaskAnswer(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskSubmissionId, @PathVariable String taskAnswerId,
                                        @Valid @RequestBody RateAnswerRequest request) {
        return taskAnswerService.rateTaskAnswer(courseId, assignmentId, taskSubmissionId, taskAnswerId, request);
    }

    @GetMapping("/submissions/{taskSubmissionId}/task-answers/unchecked-answers")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<TaskAnswerDTO> getUncheckedTaskAnswers(@PathVariable String courseId, @PathVariable String assignmentId, @PathVariable String taskSubmissionId) {
        return taskAnswerService.getUncheckedTaskAnswers(courseId, assignmentId, taskSubmissionId);
    }
}
