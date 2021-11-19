package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.CourseService;
import com.hubex.learningsystem.app.models.dtos.CourseDTO;
import com.hubex.learningsystem.app.models.dtos.CourseDetails;
import com.hubex.learningsystem.app.models.dtos.LessonDTO;
import com.hubex.learningsystem.app.models.dtos.PersonDTO;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.entities.PersonEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                return new CreateCourseResponse("Niepoprawna nazwa kursu", "ERROR");
            }
            return new CreateCourseResponse("Z powodzeniem utworzono kurs", "SUCCESS");
        }
    }

    @Override
    public GetAllCoursesResponse getAllCourses() {
        List<CourseDTO> courses = courseRepository.findAll().stream().map(item -> {
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
    public CourseDetails getCourseDetails(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if(loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<LessonEntity> lessons = new ArrayList<>(courseRepository.findById(Long.valueOf(courseId)).get().getLessons());
            List<LessonDTO> lessonDTOS = lessons.stream().map(lesson -> modelMapper.map(lesson, LessonDTO.class)).collect(Collectors.toList());
            CourseDetails courseDetails = new CourseDetails();
            courseDetails.setLessons(lessonDTOS);
            return courseDetails;
        }
    }
}
