package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.ExamServiceImpl;
import com.hubex.learningsystem.app.models.dtos.ExamDTO;
import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class ExamController {
    private final ExamServiceImpl examService;

    public ExamController(ExamServiceImpl examService) {
        this.examService = examService;
    }

    @PostMapping("/{courseId}/exams")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createExam(@Valid @RequestBody CreateExamRequest examRequest, @PathVariable String courseId) {
        UniversalResponse response = examService.createExam(examRequest, courseId);
        if (response.getStatus().equals("ERROR")) {
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}/exams/{examId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteExam(@PathVariable String courseId, @PathVariable String examId) {
        UniversalResponse response = examService.deleteExam(courseId, examId);
        if (response.getStatus().equals("ERROR")) {
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unchecked-exams")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<ExamDTO> getUncheckedExams() {
        return examService.getUncheckedExams();
    }

    @GetMapping("/pending-exams")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public List<ExamDTO> getPendingExams() {
        return examService.getPendingExams();
    }

    @GetMapping("/{courseId}/exams")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<ExamDTO> getCourseExams(@PathVariable String courseId) {
        return examService.getCourseExams(courseId);
    }
}
