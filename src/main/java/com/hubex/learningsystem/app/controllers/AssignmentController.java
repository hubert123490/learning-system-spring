package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.AssignmentServiceImpl;
import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {
    private final AssignmentServiceImpl assignmentService;


    public AssignmentController(AssignmentServiceImpl assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
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

    @DeleteMapping("/{assignmentId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteExam(@PathVariable String courseId, @PathVariable String assignmentId) {
        UniversalResponse response = assignmentService.deleteAssignment(courseId, assignmentId);
        if (response.getStatus().equals("ERROR")) {
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }
}
