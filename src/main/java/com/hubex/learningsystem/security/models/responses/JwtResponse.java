package com.hubex.learningsystem.security.models.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private List<String> roles;
    private String firstName;
    private String lastName;
    private String title;

    public JwtResponse(String accessToken, Long id, String email, List<String> roles, String firstName, String lastName, String title) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
    }
}
