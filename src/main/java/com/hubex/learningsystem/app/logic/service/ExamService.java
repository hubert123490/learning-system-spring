package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.ExamDTO;
import com.hubex.learningsystem.app.models.requests.ChangeDatesRequest;
import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;

import java.util.List;

public interface ExamService {
    UniversalResponse createExam(CreateExamRequest request, String courseId);
    UniversalResponse deleteExam(String courseId, String examId);
    List<ExamDTO> getUncheckedExams();
    List<ExamDTO> getPendingExams();
    List<ExamDTO> getCourseExams(String courseId);
    UniversalResponse changeExamDates(String courseId, String examId, ChangeDatesRequest request);
    //List<ExamDTO> testFunc();
}
