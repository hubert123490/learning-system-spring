package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.requests.CreateLessonRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

public interface LessonService {
    UniversalResponse createLesson(CreateLessonRequest request, String courseId);
    UniversalResponse deleteLesson(String lessonId, String courseId);
}
