package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.SubmissionService;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.QuestionEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.SubmissionRepository;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(UserRepository userRepository, ExamRepository examRepository, SubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public UniversalResponse makeSubmission(String courseId, String examId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if(exam == null){
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id");
            }

            SubmissionEntity submission = new SubmissionEntity();
            submission.setExam(exam);
            submission.setClosed(false);
            int maxScore = 0;
            for (QuestionEntity question:
                 exam.getQuestions()) {
                if(question.getType().equals("textarea")) {
                    maxScore += question.getMaxPoints();
                }
                if(question.getCorrectAnswer() != null && !question.getCorrectAnswer().isEmpty())
                    maxScore += question.getMaxPoints();
            }
            submission.setMaxScore(maxScore);
            submission.setStartDate(LocalDateTime.now());
            submission.setStudent(loggedUser);

            try {
                submissionRepository.save(submission);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Przystąpiono do egzaminu", "SUCCESS", submission.getId());
        }
    }

    @Override
    public UniversalResponse checkSubmission(String courseId, String examId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            System.out.println("error security");
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id");
            }

            SubmissionEntity submission = submissionRepository.findByStudent_EmailAndExam(loggedUser.getEmail(), exam);
            if (submission == null) {
                return new UniversalResponse("Możesz podejść do egzaminu", "SUCCESS");
            }
            else if(!submission.isClosed()){
                return new UniversalResponse("Egzamin nadal trwa", "PENDING");
            }
            return new UniversalResponse("Uzyskana liczba punktów: " + submission.getStudentScore() + " na " + submission.getMaxScore(), "ERROR");
        }
    }
}
