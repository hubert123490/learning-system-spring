package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.CourseService;
import com.hubex.learningsystem.app.models.dtos.*;
import com.hubex.learningsystem.app.models.entities.*;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.app.models.responses.StudentCourseGrades;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public CourseServiceImpl(UserRepository userRepository, CourseRepository courseRepository, LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public CreateCourseResponse createCourse(CreateCourseRequest courseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            CourseEntity course = modelMapper.map(courseRequest, CourseEntity.class);
            course.getTeachers().add(loggedUser);
            loggedUser.getTeacherCourses().add(course);


            try {
                courseRepository.save(course);
            } catch (Exception e) {
                return new CreateCourseResponse("Niepoprawna nazwa kursu", "ERROR", null);
            }
            return new CreateCourseResponse("Z powodzeniem utworzono kurs", "SUCCESS", course.getId());
        }
    }

    @Override
    public GetAllCoursesResponse getAllCourses(String name, String category, String lastName) {
        List<CourseDTO> courses = courseRepository.findAllByNameAndCategory(name, category, lastName).stream().map(item -> {
            CourseDTO returnCourse = modelMapper.map(item, CourseDTO.class);
            List<PersonEntity> persons = item.getTeachers().stream().map(UserEntity::getPerson).collect(Collectors.toList());
            List<PersonDTO> personsDTO = persons.stream().map(person -> modelMapper.map(person, PersonDTO.class)).collect(Collectors.toList());
            returnCourse.setPerson(personsDTO);
            return returnCourse;
        }).collect(Collectors.toList());
        return new GetAllCoursesResponse(courses);

    }

    @Override
    public GetAllCoursesResponse getTeacherCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<CourseDTO> myCourses = courseRepository.findAllByTeachersEmail(loggedUser.getEmail()).stream().map(item -> modelMapper.map(item, CourseDTO.class)).collect(Collectors.toList());

            return new GetAllCoursesResponse(myCourses);
        }
    }

    @Override
    public GetAllCoursesResponse getStudentCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<CourseDTO> myCourses = courseRepository.findAllByStudentsEmail(loggedUser.getEmail()).stream().map(item -> modelMapper.map(item, CourseDTO.class)).collect(Collectors.toList());

            return new GetAllCoursesResponse(myCourses);
        }
    }

    @Override
    public CourseDetails getCourseDetails(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId))) &&
                loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");

        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            if (course == null) {
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }
            List<LessonEntity> lessons = new ArrayList<>(course.getLessons());
            List<ExamEntity> exams = new ArrayList<>(course.getExams());
            List<LessonDTO> lessonDTOS = lessons.stream().map(lesson -> modelMapper.map(lesson, LessonDTO.class)).collect(Collectors.toList());
            List<ExamDTO> examDTOS = exams.stream().map(exam -> modelMapper.map(exam, ExamDTO.class)).collect(Collectors.toList());
            CourseDetails courseDetails = new CourseDetails();
            courseDetails.setLessons(lessonDTOS);
            courseDetails.setExams(examDTOS);
            return courseDetails;
        }
    }

    @Override
    public UniversalResponse deleteCourse(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            CourseEntity courseToDelete = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            try {
                courseRepository.delete(courseToDelete);
            } catch (Exception e) {
                e.printStackTrace();
                return new UniversalResponse("Operacja nie powiodła się", "ERROR");
            }
        }
        return new UniversalResponse("Z powodzeniem usunięto kurs", "SUCCESS");
    }

    @Override
    public UniversalResponse enrollInCourse(String courseId, String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            if (course == null) {
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }

            if (course.getPassword().equals(password) || course.getPassword().isEmpty())
                course.getStudents().add(loggedUser);
            else
                throw new RuntimeException("Nieprawidłowe hasło");

            try {
                courseRepository.save(course);
            } catch (Exception e) {
                return new UniversalResponse("Operacja nie powiodła się", "ERROR");
            }
            return new UniversalResponse("Zapisano do kursu", "SUCCESS");
        }
    }

    @Override
    public StudentCourseGrades getStudentsGrades(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            if(course == null){
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }

            StudentCourseGrades studentGrades = new StudentCourseGrades();
            List<UserEntity> students = new ArrayList<>(course.getStudents());
            for (UserEntity student:
                 students) {
                StudentGradeDTO studentDTO = new StudentGradeDTO();
                studentDTO.setStudentId(student.getId());
                studentDTO.setEmail(student.getEmail());
                studentDTO.setFirstName(student.getPerson().getFirstName());
                studentDTO.setLastName(student.getPerson().getLastName());

                List<ExamGradeDTO> exams = course.getExams().stream().map(examEntity -> {
                    List<SubmissionEntity> submissions = examEntity.getSubmissions().stream()
                            .filter(submissionEntity -> submissionEntity.getStudent() == student).collect(Collectors.toList());
                    ExamGradeDTO returnValue = new ExamGradeDTO();
                    returnValue.setExamId(examEntity.getId());
                    returnValue.setExamName(examEntity.getName());
                    returnValue.setMaxPoints(examEntity.getQuestions().stream().mapToInt(QuestionEntity::getMaxPoints).sum());
                    if(submissions.isEmpty()) {
                        returnValue.setStudentPoints(0);
                        returnValue.setStatus("NOT_SUBMITTED");
                    }
                    else {
                        returnValue.setStudentPoints(submissions.stream().mapToInt(SubmissionEntity::getStudentScore).sum());
                        returnValue.setStatus("SUBMITTED");
                    }
                    return returnValue;
                }).collect(Collectors.toList());
                studentDTO.setExams(exams);


                studentDTO.setStudentPoints(student.getSubmissions().stream().filter(submissionEntity -> course.getExams().stream().anyMatch(examEntity -> examEntity == submissionEntity.getExam()))
                        .mapToInt(SubmissionEntity::getStudentScore).sum());
                studentDTO.setCoursePoints(course.getExams().stream().mapToInt(value -> value.getQuestions().stream().mapToInt(QuestionEntity::getMaxPoints).sum()).sum());
                studentDTO.setGrade(convertGrade((double)studentDTO.getStudentPoints()/studentDTO.getCoursePoints()));
                studentGrades.getStudentsGrades().add(studentDTO);
            }
            return studentGrades;
        }
    }

    private double convertGrade(double grade) {
        if(grade < 0.5)
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


