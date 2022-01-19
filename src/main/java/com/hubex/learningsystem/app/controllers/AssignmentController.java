package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.AssignmentServiceImpl;
import com.hubex.learningsystem.app.models.dtos.AssignmentDTO;
import com.hubex.learningsystem.app.models.dtos.ExamDTO;
import com.hubex.learningsystem.app.models.requests.ChangeDatesRequest;
import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class AssignmentController {
    private final AssignmentServiceImpl assignmentService;


    public AssignmentController(AssignmentServiceImpl assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/{courseId}/assignments")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createAssignment(@Valid @RequestBody CreateAssignmentRequest request, @PathVariable String courseId) {
        UniversalResponse response = assignmentService.createAssignment(request, courseId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}/assignments/{assignmentId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteAssignment(@PathVariable String courseId, @PathVariable String assignmentId) {
        UniversalResponse response = assignmentService.deleteAssignment(courseId, assignmentId);
        if (response.getStatus().equals("ERROR")) {
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignments/pending-assignments")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public List<AssignmentDTO> getPendingAssignments() {
        return assignmentService.getPendingAssignments();
    }

    @GetMapping("/{courseId}/assignments")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<AssignmentDTO> getCourseAssignments(@PathVariable String courseId) {
        return assignmentService.getCourseAssignments(courseId);
    }

    @GetMapping("/assignments/unchecked-assignments")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public List<AssignmentDTO> getUncheckedAssignments() {
        return assignmentService.getUncheckedAssignments();
    }

    @PatchMapping("/{courseId}/assignments/{assignmentId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> changeAssignmentDates(@PathVariable String courseId, @PathVariable String assignmentId, @RequestBody @Valid ChangeDatesRequest request) {
        UniversalResponse response = assignmentService.changeAssignmentDates(courseId, assignmentId, request);
        return ResponseEntity.ok(response);
    }
}
