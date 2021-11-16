package com.hubex.learningsystem.security.models.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 120)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 120)
    private String lastName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String title;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> role;
}
