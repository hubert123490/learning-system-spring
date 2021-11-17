package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.CourseServiceImpl;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseServiceImpl courseService;

    public CourseController(CourseServiceImpl courseService){
        this.courseService = courseService;
    }

    @PostMapping("/create-course")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CreateCourseRequest courseRequest) {
        CreateCourseResponse response = courseService.createCourse(courseRequest);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<?> getAllCourses() {
        GetAllCoursesResponse response = courseService.getAllCourses();
        if(response.getCourses().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        } else if (!response.getCourses().isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }
}
