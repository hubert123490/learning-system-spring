package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.SubmissionService;
import com.hubex.learningsystem.app.models.dtos.SubmissionDTO;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.QuestionEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.SubmissionRepository;
import com.hubex.learningsystem.app.models.responses.CheckSubmissionResponse;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper = new ModelMapper();

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

            if(LocalDateTime.now().isBefore(exam.getStartDate()) || LocalDateTime.now().isAfter(exam.getEndDate())){
                return new UniversalResponse("Sprawdź czas podejścia do egzaminu", "ERROR");
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
    public CheckSubmissionResponse checkSubmission(String courseId, String examId) {
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
            if (submission == null && (LocalDateTime.now().isAfter(exam.getStartDate()) && LocalDateTime.now().isBefore(exam.getEndDate()))) {
                return new CheckSubmissionResponse("Możesz podejść do egzaminu", "SUCCESS", exam.getStartDate(), exam.getEndDate());
            }
            else if(LocalDateTime.now().isAfter(exam.getEndDate())) {
                return new CheckSubmissionResponse("Egzamin zakończył się", "ENDED", exam.getStartDate(), exam.getEndDate());
            }
            else if(!submission.isClosed()){
                return new CheckSubmissionResponse("Egzamin nadal trwa", "PENDING", exam.getStartDate(), exam.getEndDate());
            }
            else if(submission.getAnswers().stream().anyMatch(answer -> !answer.isChecked())) {
                return new CheckSubmissionResponse("Egzamin nadal wymaga sprawdzenia przez nauczyciela", "CHECKING", exam.getStartDate(), exam.getEndDate());
            }
            return new CheckSubmissionResponse("Uzyskana liczba punktów: " + submission.getStudentScore() + " na " + submission.getMaxScore(), "ERROR", exam.getStartDate(), exam.getEndDate());
        }
    }

    @Override
    public List<SubmissionDTO> findSubmissions(String courseId, String examId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            System.out.println("error security");
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id");
            }

            List<SubmissionDTO> submissions = exam.getSubmissions().stream()
                    .filter((item) -> item.getAnswers().stream()
                            .anyMatch(answer -> !answer.isChecked()))
                    .map(answer -> {
                        SubmissionDTO returnValue = modelMapper.map(answer, SubmissionDTO.class);
                        returnValue.setStudentFirstName(answer.getStudent().getPerson().getFirstName());
                        returnValue.setStudentLastName(answer.getStudent().getPerson().getLastName());
                        return returnValue;
                    })
                    .collect(Collectors.toList());


            return submissions;
        }
    }
}
