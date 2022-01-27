package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.requests.CreateLessonRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import com.hubex.learningsystem.security.principal.UserPrincipal;
import org.assertj.core.api.Assertions;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LessonServiceImplTest {
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
    void createLesson_throwsSecurityException() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        UserEntity user = new UserEntity();
        CreateLessonRequest request = new CreateLessonRequest();

        //set required data
        request.setName("test");
        request.setDescription("test");

        user.setEmail("test@mail.com");

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(lessonRepository.save(any(LessonEntity.class))).then(i -> {
            LessonEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        LessonServiceImpl SUT = new LessonServiceImpl(userRepository, lessonRepository, courseRepository);

        //when
        UniversalResponse expectedResponse = new UniversalResponse("Z powodzeniem utworzono lekcję", "SUCCESS", 1L);
        var exception = catchThrowable(() -> SUT.createLesson(request, "1"));

        //then
        assertThat(exception).isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Should create lesson without errors")
    void createLesson_worksAsExpected() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        UserEntity user = new UserEntity();
        CreateLessonRequest request = new CreateLessonRequest();

        CourseEntity ownedCourse = new CourseEntity();
        ownedCourse.setLessons(new HashSet<>());
        ownedCourse.setId(1L);

        //set required data
        request.setName("test");
        request.setDescription("test");

        user.setEmail("test@mail.com");
        user.setTeacherCourses(new HashSet<>(Collections.singletonList(ownedCourse)));

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(lessonRepository.save(any(LessonEntity.class))).then(i -> {
            LessonEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(ownedCourse));
        LessonServiceImpl SUT = new LessonServiceImpl(userRepository, lessonRepository, courseRepository);

        //when
        UniversalResponse expectedResponse = new UniversalResponse("Z powodzeniem utworzono lekcję", "SUCCESS", 1L);
        UniversalResponse response =  SUT.createLesson(request, "1");

        //then
        assertThat(expectedResponse.getStatus()).isEqualTo(response.getStatus());
    }
}