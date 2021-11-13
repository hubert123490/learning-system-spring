package com.hubex.learningsystem.security.logic.service;

import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.requests.LoginRequest;
import com.hubex.learningsystem.security.models.requests.SignupRequest;
import com.hubex.learningsystem.security.models.responses.JwtResponse;
import com.hubex.learningsystem.security.models.responses.MessageResponse;

public interface AuthenticationService {
    MessageResponse registerUser(SignupRequest newUser);
    JwtResponse loginUser(LoginRequest loginRequest);
}
