package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.SubmissionServiceImpl;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/exams/{examId}/submissions")
public class SubmissionController {
    private final SubmissionServiceImpl submissionService;

    public SubmissionController(SubmissionServiceImpl submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping()
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public UniversalResponse makeSubmission(@PathVariable String courseId, @PathVariable String examId) {
        return submissionService.makeSubmission(courseId, examId);
    }

    @GetMapping()
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public UniversalResponse checkSubmission(@PathVariable String courseId, @PathVariable String examId) {
        return submissionService.checkSubmission(courseId, examId);
    }
}
