package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.AnswerServiceImpl;
import com.hubex.learningsystem.app.models.requests.SubmitAnswersRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/exams/{examId}/submissions/{submissionId}")
public class AnswerController {
    private final AnswerServiceImpl answerService;

    public AnswerController(AnswerServiceImpl answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/submit-answers")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public UniversalResponse submitAnswers(@PathVariable String courseId, @PathVariable String examId,@PathVariable String submissionId, @RequestBody @Valid SubmitAnswersRequest request) {
        return answerService.submitAnswers(courseId, examId, submissionId, request);
    }
}
