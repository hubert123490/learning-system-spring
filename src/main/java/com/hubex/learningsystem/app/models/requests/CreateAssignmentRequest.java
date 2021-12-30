package com.hubex.learningsystem.app.models.requests;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAssignmentRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
