package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class StudentGradeDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 4304110058679853093L;

    private Long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private int studentPoints;
    private int coursePoints;
    private double grade;
    private List<ExamGradeDTO> exams;
}
