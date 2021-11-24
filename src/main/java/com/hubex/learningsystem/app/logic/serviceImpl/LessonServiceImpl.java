package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.LessonService;
import com.hubex.learningsystem.app.models.dtos.LessonDTO;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
import com.hubex.learningsystem.app.models.requests.CreateLessonRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class LessonServiceImpl implements LessonService {
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public LessonServiceImpl(UserRepository userRepository, LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public UniversalResponse createLesson(CreateLessonRequest request, String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            LessonEntity lesson = modelMapper.map(request, LessonEntity.class);
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            lesson.setCourse(course);
            course.getLessons().add(lesson);

            try {
                lessonRepository.save(lesson);
            } catch (Exception e) {

            }
            return new UniversalResponse("Z powodzeniem utworzono lekcję", "SUCCESS", lesson.getId());
        }
    }

    @Override
    public UniversalResponse deleteLesson(String lessonId, String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
       }
        else {
            LessonEntity lessonToDelete = lessonRepository.findById(Long.valueOf(lessonId)).orElse(null);
            System.out.println(lessonToDelete);

            try {
                    lessonRepository.delete(lessonToDelete);
            } catch (Exception e) {
                e.printStackTrace();
                return new UniversalResponse("Operacja nie powiodła się", "ERROR");
            }
        }
        return new UniversalResponse("Z powodzeniem usunięto lekcję", "SUCCESS");
    }

    @Override
    public LessonDTO getLesson(String lessonId, String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        }
        else {
            LessonEntity lesson = lessonRepository.findById(Long.valueOf(lessonId)).orElse(null);
            if (lesson == null) {
                throw new NullPointerException("Nie znaleziono lekcji");
            } else {
                LessonDTO returnValue = new LessonDTO();
                returnValue.setId(lesson.getId());
                returnValue.setName(lesson.getName());
                return returnValue;
            }
        }
    }
}