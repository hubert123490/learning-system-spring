package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.CourseDetails;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;

public interface CourseService {
    CreateCourseResponse createCourse(CreateCourseRequest courseRequest);
    GetAllCoursesResponse getAllCourses();
    GetAllCoursesResponse getTeacherCourses();
    CourseDetails getCourseDetails(String id);
}
