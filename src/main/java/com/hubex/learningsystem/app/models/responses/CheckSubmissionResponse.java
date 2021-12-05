package com.hubex.learningsystem.app.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CheckSubmissionResponse {
    private String message;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
