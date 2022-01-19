package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 4918721752591729052L;

    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private String courseName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
