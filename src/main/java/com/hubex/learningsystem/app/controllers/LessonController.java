package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.LessonServiceImpl;
import com.hubex.learningsystem.app.models.requests.CreateLessonRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}")
public class LessonController {
    private final LessonServiceImpl lessonService;

    public LessonController(LessonServiceImpl lessonService){
        this.lessonService = lessonService;
    }

    @PostMapping("/create-lesson")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createLesson(@Valid @RequestBody CreateLessonRequest lessonRequest, @PathVariable String courseId) {
        UniversalResponse response = lessonService.createLesson(lessonRequest, courseId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-lesson/{lessonId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteLesson(@PathVariable String courseId, @PathVariable String lessonId) {
        UniversalResponse response = lessonService.deleteLesson( lessonId, courseId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }
}
