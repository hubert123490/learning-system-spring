package com.hubex.learningsystem.app.models.responses;

import com.hubex.learningsystem.app.models.dtos.StudentGradeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudentCourseGrades {
    List<StudentGradeDTO> studentsGrades = new ArrayList<>();
}
