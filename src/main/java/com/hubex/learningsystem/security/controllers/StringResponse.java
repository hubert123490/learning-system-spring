package com.hubex.learningsystem.security.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringResponse {
    StringResponse(String message){
        this.message = message;
    }
    private String message;
}
