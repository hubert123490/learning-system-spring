package com.hubex.learningsystem.security.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/student")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public StringResponse userAccess() {
        return new StringResponse("Panel studenta");
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public StringResponse moderatorAccess() {
        return new StringResponse("Panel nauczyciela");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public StringResponse adminAccess() {
        return new StringResponse("Panel admina");
    }
}
