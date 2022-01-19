package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AssignmentGradeDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -4236656407334850023L;

    private Long assignmentId;
    private String assignmentName;
    private int maxPoints;
    private int studentPoints;
    private String status;
}
