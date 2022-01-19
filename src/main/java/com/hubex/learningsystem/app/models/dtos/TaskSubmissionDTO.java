package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskSubmissionDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 6541170738260587795L;

    private Long id;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private boolean checkedOnce;
    private Long assignmentId;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private LocalDateTime submissionDate;
}
