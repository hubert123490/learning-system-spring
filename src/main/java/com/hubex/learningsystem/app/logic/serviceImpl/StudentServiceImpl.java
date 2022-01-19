package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.StudentService;
import com.hubex.learningsystem.app.models.dtos.AssignmentGradeDTO;
import com.hubex.learningsystem.app.models.dtos.CourseGradeDTO;
import com.hubex.learningsystem.app.models.dtos.ExamGradeDTO;
import com.hubex.learningsystem.app.models.entities.*;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.responses.StudentGrades;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public StudentServiceImpl(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public StudentGrades getGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }

        StudentGrades studentGrades = new StudentGrades();
        List<CourseEntity> courses = courseRepository.findAllByStudentsEmail(loggedUser.getEmail());
        for (CourseEntity course :
                courses) {
            CourseGradeDTO courseGrade = new CourseGradeDTO();
            courseGrade.setCourseId(course.getId());
            courseGrade.setCourseName(course.getName());

            //exams
            courseGrade.setCourseExamPoints(course.getExams().stream().mapToInt(value -> value.getQuestions().stream()
                    .mapToInt(QuestionEntity::getMaxPoints).sum()).sum());
            courseGrade.setStudentExamPoints(loggedUser.getSubmissions().stream().filter(submissionEntity -> course.getExams()
                    .stream().anyMatch(examEntity -> examEntity == submissionEntity.getExam()))
                    .mapToInt(SubmissionEntity::getStudentScore).sum());
            courseGrade.setExamGrade(convertGrade((double) courseGrade.getStudentExamPoints() / courseGrade.getCourseExamPoints()));

            List<ExamGradeDTO> exams = course.getExams().stream().map(examEntity -> {
                List<SubmissionEntity> submissions = examEntity.getSubmissions().stream()
                        .filter(submissionEntity -> submissionEntity.getStudent() == loggedUser).collect(Collectors.toList());
                ExamGradeDTO returnValue = new ExamGradeDTO();
                returnValue.setExamId(examEntity.getId());
                returnValue.setExamName(examEntity.getName());
                returnValue.setMaxPoints(examEntity.getQuestions().stream().mapToInt(QuestionEntity::getMaxPoints).sum());
                if (submissions.isEmpty()) {
                    returnValue.setStudentPoints(0);
                    returnValue.setStatus("NOT_SUBMITTED");
                } else {
                    returnValue.setStudentPoints(submissions.stream().mapToInt(SubmissionEntity::getStudentScore).sum());
                    returnValue.setStatus("SUBMITTED");
                }
                return returnValue;
            }).collect(Collectors.toList());
            courseGrade.setExams(exams);


            //assignments
            courseGrade.setCourseAssignmentPoints(course.getAssignments().stream().mapToInt(value -> value.getTasks().stream()
                    .mapToInt(TaskEntity::getMaxPoints).sum()).sum());
            courseGrade.setStudentAssignmentPoints(loggedUser.getTaskSubmissions().stream().filter(taskSubmissionEntity -> course.getAssignments()
            .stream().anyMatch(assignmentEntity -> assignmentEntity == taskSubmissionEntity.getAssignment()))
            .mapToInt(TaskSubmissionEntity::getStudentScore).sum());
            courseGrade.setAssignmentGrade(convertGrade((double) courseGrade.getStudentAssignmentPoints() / courseGrade.getCourseAssignmentPoints()));

            List<AssignmentGradeDTO> assignments = course.getAssignments().stream().map(assignmentEntity -> {
                List<TaskSubmissionEntity> submissions = assignmentEntity.getTaskSubmissions().stream()
                        .filter(taskSubmissionEntity -> taskSubmissionEntity.getStudent() == loggedUser).collect(Collectors.toList());
                AssignmentGradeDTO returnValue = new AssignmentGradeDTO();
                returnValue.setAssignmentId(assignmentEntity.getId());
                returnValue.setAssignmentName(assignmentEntity.getName());
                returnValue.setMaxPoints(assignmentEntity.getTasks().stream().mapToInt(TaskEntity::getMaxPoints).sum());
                if(submissions.isEmpty()) {
                    returnValue.setStudentPoints(0);
                    returnValue.setStatus("NOT_SUBMITTED");
                } else {
                    returnValue.setStudentPoints(submissions.stream().mapToInt(TaskSubmissionEntity::getStudentScore).sum());
                    returnValue.setStatus("SUBMITTED");
                }
                return returnValue;
            }).collect(Collectors.toList());
            courseGrade.setAssignments(assignments);

            studentGrades.getGrades().add(courseGrade);
        }
        return studentGrades;
    }

    private double convertGrade(double grade) {
        if (grade < 0.5)
            return 2;
        else if (grade >= 0.5 && grade < 0.6)
            return 3;
        else if (grade >= 0.6 && grade < 0.7)
            return 3.5;
        else if (grade >= 0.7 && grade < 0.8)
            return 4;
        else if (grade >= 0.8 && grade < 0.9)
            return 4.5;
        else if (grade >= 0.9 && grade <= 1.0)
            return 5;
        else
            return -1;
    }
}
