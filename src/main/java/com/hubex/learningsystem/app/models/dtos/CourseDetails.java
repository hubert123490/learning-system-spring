package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CourseDetails implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5566666150667358804L;

    private List<LessonDTO> lessons;
    private List<ExamDTO> exams;
    private String message;
}
