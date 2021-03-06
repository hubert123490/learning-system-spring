package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CourseGradeDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -498371826411893378L;

    private Long courseId;
    private String courseName;
    private int studentExamPoints;
    private int courseExamPoints;
    private double examGrade;
    private int studentAssignmentPoints;
    private int courseAssignmentPoints;
    private double assignmentGrade;
    private List<ExamGradeDTO> exams;
    private List<AssignmentGradeDTO> assignments;
}
