package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.QuestionServiceImpl;
import com.hubex.learningsystem.app.models.dtos.QuestionDTO;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRadioRequest;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/exams/{examId}/questions")
public class QuestionController {
    private final QuestionServiceImpl questionService;

    public QuestionController(QuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    @GetMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public List<QuestionDTO> getQuestions(@PathVariable String courseId, @PathVariable String examId) {
        return questionService.getQuestions(courseId, examId);
    }

    @PostMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addQuestion(@PathVariable String courseId, @PathVariable String examId, @RequestBody @Valid CreateQuestionRequest request) {
        return questionService.addQuestion(courseId, examId, request);
    }

    @DeleteMapping("/{questionId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse deleteContent(@PathVariable String courseId, @PathVariable String examId, @PathVariable String questionId) {
        return questionService.deleteQuestion(courseId, examId, questionId);
    }

    @PostMapping("/{questionId}/create-radio")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addQuestionRadio(@PathVariable String courseId, @PathVariable String examId, @PathVariable String questionId, @RequestBody @Valid CreateQuestionRadioRequest request) {
        return questionService.addQuestionRadio(courseId, examId, questionId, request);
    }

    @PostMapping("/{questionId}/create-text")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addQuestionText(@PathVariable String courseId, @PathVariable String examId, @PathVariable String questionId, @RequestBody @Valid String correctAnswer) {
        return questionService.addQuestionText(courseId, examId, questionId, correctAnswer);
    }

    @PostMapping("/{questionId}/create-text-area")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addQuestionTextArea(@PathVariable String courseId, @PathVariable String examId, @PathVariable String questionId, @RequestBody @Valid String correctAnswer) {
        return questionService.addQuestionTextArea(courseId, examId, questionId, correctAnswer);
    }
}
