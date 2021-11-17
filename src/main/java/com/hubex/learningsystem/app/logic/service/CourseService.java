package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface CourseService {
    CreateCourseResponse createCourse(CreateCourseRequest courseRequest);
    GetAllCoursesResponse getAllCourses();
}
