package com.hubex.learningsystem.app.models.responses;

import com.hubex.learningsystem.app.models.dtos.CourseGradeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudentGrades {
    private List<CourseGradeDTO> grades = new ArrayList<>();
}
