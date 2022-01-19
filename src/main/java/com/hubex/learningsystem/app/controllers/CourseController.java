package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.CourseServiceImpl;
import com.hubex.learningsystem.app.models.dtos.CourseDetails;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.app.models.responses.StudentCourseGrades;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.http.HttpStatus;
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

    @PostMapping()
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
    public ResponseEntity<?> getAllCourses(@RequestParam(required = false) String name, @RequestParam(required = false) String category,
                                           @RequestParam(required = false) String lastName) {
        GetAllCoursesResponse response = courseService.getAllCourses(name, category, lastName);
        if(response.getCourses().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        } else if (!response.getCourses().isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-courses-teacher")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public ResponseEntity<?> getMyCourses() {
        GetAllCoursesResponse response = courseService.getTeacherCourses();
        if(response.getCourses().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        } else if (!response.getCourses().isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-courses-student")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseBody
    public ResponseEntity<?> getMyCoursesStudent() {
        GetAllCoursesResponse response = courseService.getStudentCourses();
        if(response.getCourses().isEmpty()){
            return ResponseEntity.badRequest().body(response);
        } else if (!response.getCourses().isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    @ResponseBody
    public ResponseEntity<?> getCourseDetails(@PathVariable String courseId) {
        try {
            CourseDetails response = courseService.getCourseDetails(courseId);
            if(response.getLessons().isEmpty()){
                return ResponseEntity.ok(response);
            } else if (!response.getLessons().isEmpty()) {
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            UniversalResponse response = new UniversalResponse(e.getMessage(), "PROTECTED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        UniversalResponse response = courseService.deleteCourse(courseId);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (!response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseBody
    public UniversalResponse enrollInCourse(@PathVariable String courseId, @RequestBody String password){
        return courseService.enrollInCourse(courseId, password);
    }

    @GetMapping("/{courseId}/students-grades")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public StudentCourseGrades getStudentsGrades(@PathVariable String courseId) {
        return courseService.getStudentsGrades(courseId);
    }
}
