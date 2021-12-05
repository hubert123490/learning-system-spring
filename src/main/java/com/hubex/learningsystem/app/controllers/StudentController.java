package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.StudentServiceImpl;
import com.hubex.learningsystem.app.models.responses.StudentGrades;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class StudentController {
    private final StudentServiceImpl studentService;

    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseBody
    public StudentGrades getGrades() {
        return studentService.getGrades();
    }

}
