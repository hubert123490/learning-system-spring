package com.hubex.learningsystem.security.controllers;

import com.hubex.learningsystem.security.logic.serviceImpl.AuthenticationServiceImpl;
import com.hubex.learningsystem.security.models.requests.LoginRequest;
import com.hubex.learningsystem.security.models.requests.SignupRequest;
import com.hubex.learningsystem.security.models.responses.JwtResponse;
import com.hubex.learningsystem.security.models.responses.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationServiceImpl authenticationService;

    public AuthenticationController(AuthenticationServiceImpl authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse response = authenticationService.registerUser(signupRequest);
        if(response.getStatus().equals("ERROR")){
            return ResponseEntity.badRequest().body(response);
        } else if (response.getStatus().equals("SUCCESS")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse user = authenticationService.loginUser(loginRequest);
        return ResponseEntity.ok(user);
    }

}
