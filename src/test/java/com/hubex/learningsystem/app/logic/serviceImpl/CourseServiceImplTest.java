package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.models.dtos.CourseDTO;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateCourseRequest;
import com.hubex.learningsystem.app.models.responses.CreateCourseResponse;
import com.hubex.learningsystem.app.models.responses.GetAllCoursesResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import com.hubex.learningsystem.security.principal.UserPrincipal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.mustache.Model;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseServiceImplTest {
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
    @DisplayName("Should create course without errors")
    void createCourse_worksAsExpected() {
        //given
        CreateCourseRequest createCourseRequest = new CreateCourseRequest();
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        UserEntity user = new UserEntity();
        CourseEntity testEntity = new CourseEntity();
        testEntity.setId(1L);

        //set required data
        createCourseRequest.setName("testName");
        createCourseRequest.setPassword("testPass");
        createCourseRequest.setCategory("testCategory");

        user.setEmail("test@mail.com");


        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(courseRepository.save(any(CourseEntity.class))).then(i -> {
            CourseEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        CourseServiceImpl SUT = new CourseServiceImpl(userRepository, courseRepository, lessonRepository);

        //when
        CreateCourseResponse expectedResponse = new CreateCourseResponse("Z powodzeniem utworzono kurs", "SUCCESS", 1L);
        CreateCourseResponse response = SUT.createCourse(createCourseRequest);

        //then
        assertEquals(expectedResponse.getMessage(), response.getMessage());
        assertEquals(expectedResponse.getStatus(), response.getStatus());
        assertEquals(expectedResponse.getId(), response.getId());
    }

    @Test
    @DisplayName("Should throw RuntimeException for not logged in User")
    void createCourse_notLoggedIn() throws RuntimeException{
        //given
        CreateCourseRequest createCourseRequest = new CreateCourseRequest();
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        CourseEntity testEntity = new CourseEntity();

        //set required data
        createCourseRequest.setName("testName");
        createCourseRequest.setPassword("testPass");
        createCourseRequest.setCategory("testCategory");

        //SUT
        //mock auto increment
        when(courseRepository.save(any(CourseEntity.class))).then(i -> {
            CourseEntity entity = i.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        CourseServiceImpl SUT = new CourseServiceImpl(userRepository, courseRepository, lessonRepository);

        //when
        CreateCourseResponse expectedResponse = new CreateCourseResponse("Z powodzeniem utworzono kurs", "SUCCESS", 1L);
        var exception = catchThrowable(() -> SUT.createCourse(createCourseRequest));

        //then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw Exception")
    void createCourse_repositorySaveError() throws RuntimeException{
        //given
        CreateCourseRequest createCourseRequest = new CreateCourseRequest();
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        CourseEntity testEntity = new CourseEntity();
        UserEntity user = new UserEntity();


        //set required data
        user.setEmail("test@mail.com");

        createCourseRequest.setName("testName");
        createCourseRequest.setPassword("testPass");
        createCourseRequest.setCategory("testCategory");

        //SUT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //mock auto increment
        when(courseRepository.save(any(CourseEntity.class))).thenThrow(RuntimeException.class);
        CourseServiceImpl SUT = new CourseServiceImpl(userRepository, courseRepository, lessonRepository);

        //when
        CreateCourseResponse expectedResponse = new CreateCourseResponse("Z powodzeniem utworzono kurs", "SUCCESS", 1L);
        var exception = catchThrowable(() -> SUT.createCourse(createCourseRequest));

        //then
        assertThat(exception).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should return all courses")
    void getAllCourses_worksAsExpected() {
        //given
        UserRepository userRepository = mock(UserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        LessonRepository lessonRepository = mock(LessonRepository.class);
        List<CourseEntity> returnedCourses = new ArrayList<>();
        CourseEntity forTest1 = new CourseEntity();


        //set required data
        forTest1.setName("test");
        CourseEntity forTest2 = new CourseEntity();
        forTest2.setCategory("test");

        returnedCourses.add(forTest1);
        returnedCourses.add(forTest2);

        when(courseRepository.findAllByNameAndCategory("","","")).thenReturn(returnedCourses);

        //SUT
        CourseServiceImpl SUT = new CourseServiceImpl(userRepository, courseRepository, lessonRepository);

        //when
        GetAllCoursesResponse expectedResponse = new GetAllCoursesResponse(returnedCourses.stream().map(courseEntity ->
                modelMapper.map(courseEntity, CourseDTO.class)).collect(Collectors.toList()));
        GetAllCoursesResponse response = SUT.getAllCourses("","","");

        //then
        Assertions.assertThat(response).hasSameClassAs(expectedResponse);
        Assertions.assertThat(response.getCourses().size()).isEqualTo(expectedResponse.getCourses().size());
    }
}