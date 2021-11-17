package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.CourseService;
import com.hubex.learningsystem.app.models.dtos.CourseDTO;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.enums.Roles;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public CourseServiceImpl(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public CreateCourseResponse createCourse(CreateCourseRequest courseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("User not logged in");
        } else {
            CourseEntity course = modelMapper.map(courseRequest, CourseEntity.class);
            course.setUsers(new HashSet<>(Collections.singletonList(loggedUser)));
            loggedUser.setCourses(new HashSet<>(Collections.singletonList(course)));


            try {
                courseRepository.save(course);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new CreateCourseResponse("Course created successfully", "SUCCESS");
        }
    }

    @Override
    public GetAllCoursesResponse getAllCourses() {
        List<CourseDTO> courses = courseRepository.findAll().stream().map(item -> modelMapper.map(item, CourseDTO.class)).collect(Collectors.toList());

        return new GetAllCoursesResponse(courses);
    }
}
