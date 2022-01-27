package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateExamRequest;
import com.hubex.learningsystem.app.models.requests.CreateLessonRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import com.hubex.learningsystem.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExamServiceImplTest {
    private final ModelMapper modelMapper = new ModelMapper();

    //setting security context
    @BeforeAll
    static void beforeAll() {
        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return new UserPrincipal(1L, "test@mail.com", "123456789", List.of(new SimpleGrantedAuthority("TEACHER")));
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return String.valueOf(1);
            }
        };
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should throw Security Exception")
    void createExam_throwsSecurityException() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        ExamRepository examRepository = mock(ExamRepository.class);
        UserEntity user = new UserEntity();
        CreateExamRequest request = new CreateExamRequest();

        //set required data
        request.setName("test");
        request.setDescription("test");

        user.setEmail("test@mail.com");

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(examRepository.save(any(ExamEntity.class))).then(i -> {
            ExamEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ExamServiceImpl SUT = new ExamServiceImpl(userRepository, examRepository, courseRepository);

        //when
        var exception = catchThrowable(() -> SUT.createExam(request, "1"));

        //then
        assertThat(exception).isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Should throw Security Exception")
    void createExam_wrongDate() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        ExamRepository examRepository = mock(ExamRepository.class);
        UserEntity user = new UserEntity();
        CreateExamRequest request = new CreateExamRequest();
        CourseEntity ownedCourse = new CourseEntity();

        //set required data
        ownedCourse.setLessons(new HashSet<>());
        ownedCourse.setId(1L);

        request.setName("test");
        request.setDescription("test");
        request.setStartDate(LocalDateTime.of(2021,10,22,22,53));
        request.setEndDate(LocalDateTime.of(2021,11,22,22,53));

        user.setEmail("test@mail.com");

        user.setTeacherCourses(new HashSet<>(Collections.singletonList(ownedCourse)));

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(examRepository.save(any(ExamEntity.class))).then(i -> {
            ExamEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(ownedCourse));
        ExamServiceImpl SUT = new ExamServiceImpl(userRepository, examRepository, courseRepository);

        //when
        var exception = catchThrowable(() -> SUT.createExam(request, "1"));

        //then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should create exam")
    void createExam_worksAsExpected() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        ExamRepository examRepository = mock(ExamRepository.class);
        UserEntity user = new UserEntity();
        CreateExamRequest request = new CreateExamRequest();
        CourseEntity ownedCourse = new CourseEntity();

        //set required data
        ownedCourse.setLessons(new HashSet<>());
        ownedCourse.setId(1L);

        request.setName("test");
        request.setDescription("test");
        request.setStartDate(LocalDateTime.of(2500,10,22,22,53));
        request.setEndDate(LocalDateTime.of(3000,11,22,22,53));

        user.setEmail("test@mail.com");

        user.setTeacherCourses(new HashSet<>(Collections.singletonList(ownedCourse)));

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(examRepository.save(any(ExamEntity.class))).then(i -> {
            ExamEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(ownedCourse));
        ExamServiceImpl SUT = new ExamServiceImpl(userRepository, examRepository, courseRepository);

        //when
        UniversalResponse expectedResponse = new UniversalResponse("Z powodzeniem utworzono test", "SUCCESS", 1L);
        UniversalResponse response =  SUT.createExam(request, "1");

        //then
        assertThat(expectedResponse.getStatus()).isEqualTo(response.getStatus());
    }
}