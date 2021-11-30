package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubmissionDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 4304110058679853093L;

    private Long id;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private boolean isClosed;
    private Long examId;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
}
