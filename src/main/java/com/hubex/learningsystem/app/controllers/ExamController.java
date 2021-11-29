package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.ExamServiceImpl;
import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/exams")
public class ExamController {
    private final ExamServiceImpl examService;

    public ExamController(ExamServiceImpl examService) {
        this.examService = examService;
    }

    @PostMapping
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createExam(@Valid @RequestBody CreateExamRequest examRequest, @PathVariable String courseId) {
        UniversalResponse response = examService.createExam(examRequest, courseId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{examId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteExam(@PathVariable String courseId, @PathVariable String examId) {
        UniversalResponse response = examService.deleteExam(courseId, examId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }
}
