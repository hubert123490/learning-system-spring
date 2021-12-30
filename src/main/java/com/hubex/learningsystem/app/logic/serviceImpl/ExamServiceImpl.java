package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.ExamService;
import com.hubex.learningsystem.app.models.dtos.ExamDTO;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
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
public class ExamServiceImpl implements ExamService {
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public ExamServiceImpl(UserRepository userRepository, ExamRepository examRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public UniversalResponse createExam(CreateExamRequest request, String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ExamEntity exam = modelMapper.map(request, ExamEntity.class);

            if (LocalDateTime.now().isAfter(request.getStartDate())) {
                throw new RuntimeException("Zła data");
            }
            if (request.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Zła data");
            }
            exam.setStartDate(request.getStartDate());
            exam.setEndDate(request.getEndDate());
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            exam.setCourse(course);
            course.getExams().add(exam);

            try {
                examRepository.save(exam);
            } catch (Exception e) {

            }
            return new UniversalResponse("Z powodzeniem utworzono test", "SUCCESS", exam.getId());
        }
    }

    @Override
    public UniversalResponse deleteExam(String courseId, String examId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ExamEntity examToDelete = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (examToDelete == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id");
            }
            try {
                examRepository.delete(examToDelete);
            } catch (Exception e) {
                e.printStackTrace();
                return new UniversalResponse("Operacja nie powiodła się", "ERROR");
            }
        }
        return new UniversalResponse("Z powodzeniem usunięto egzamin", "SUCCESS");
    }

    @Override
    public List<ExamDTO> getCourseExams(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            List<ExamDTO> exams = examRepository.findAllByCourse_TeachersAndCourse_Id(loggedUser, Long.valueOf(courseId)).stream().map(exam -> modelMapper.map(exam, ExamDTO.class))
                    .collect(Collectors.toList());
            return exams;
        }
    }

    @Override
    public List<ExamDTO> getUncheckedExams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<ExamEntity> exams = examRepository.findAllByCourse_Teachers(loggedUser).stream()
                    .filter(examEntity -> {
                        List<SubmissionEntity> submissions = examEntity.getSubmissions().stream()
                                .filter(submissionEntity -> submissionEntity.getAnswers()
                                        .stream().anyMatch(answerEntity -> !answerEntity.isChecked())).collect(Collectors.toList());
                        return !submissions.isEmpty();
                    }).collect(Collectors.toList());

            return exams.stream().map(examEntity -> modelMapper.map(examEntity, ExamDTO.class)).collect(Collectors.toList());
        }
    }

    @Override
    public List<ExamDTO> getPendingExams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
//        } else {
//            List<ExamDTO> exams = examRepository.findAllByCourse_Students(loggedUser).stream().map(exam -> {
//                ExamDTO returnValue = modelMapper.map(exam, ExamDTO.class);
//                returnValue.setCourseId(exam.getCourse().getId());
//                return returnValue;
//            })
//                    .collect(Collectors.toList());
//            return exams;
//        }
        } else {
                List<ExamDTO> exams = examRepository.findAllByCourse_Students(loggedUser).stream().filter(exam ->
                        LocalDateTime.now().isAfter(exam.getStartDate()) && LocalDateTime.now().isBefore(exam.getEndDate())
                ).map(examEntity -> {
                    ExamDTO returnValue = modelMapper.map(examEntity, ExamDTO.class);
              returnValue.setCourseId(examEntity.getCourse().getId());
                return returnValue;
                }).collect(Collectors.toList());

                return exams;
            }
    }

//    @Override
//    public List<ExamDTO> testFunc() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = authentication.getName();
//
//        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);
//
//        if (loggedUser == null) {
//            throw new RuntimeException("Zaloguj się aby kontynuować");
//        } else {
//            List<ExamEntity> exams = examRepository.findAllByCourse_Teachers(loggedUser).stream()
//                    .filter(examEntity -> {
//                        List<SubmissionEntity> submissions = examEntity.getSubmissions().stream().filter(submissionEntity -> {
//                            if (submissionEntity.getAnswers().stream().anyMatch(answerEntity -> !answerEntity.isChecked()))
//                                return true;
//                            else
//                                return false;
//                        }).collect(Collectors.toList());
//                        return !submissions.isEmpty();
//                    }).collect(Collectors.toList());
//
//            return exams.stream().map(examEntity -> modelMapper.map(examEntity, ExamDTO.class)).collect(Collectors.toList());
//        }
//    }
}
