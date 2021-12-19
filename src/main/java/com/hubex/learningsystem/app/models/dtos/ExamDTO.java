package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class ExamDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -6194612300236856220L;

    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private String courseName;
}
