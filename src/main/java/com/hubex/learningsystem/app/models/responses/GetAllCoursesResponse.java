package com.hubex.learningsystem.app.models.responses;

import com.hubex.learningsystem.app.models.dtos.CourseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetAllCoursesResponse {
    private List<CourseDTO> courses = new ArrayList<>();

    public GetAllCoursesResponse(List<CourseDTO> courses){
        this.courses = courses;
    }
}
