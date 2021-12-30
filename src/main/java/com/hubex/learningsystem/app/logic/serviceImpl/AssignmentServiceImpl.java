package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.AssignmentService;
import com.hubex.learningsystem.app.models.dtos.AssignmentDTO;
import com.hubex.learningsystem.app.models.dtos.LessonDTO;
import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.AssignmentRepository;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public AssignmentServiceImpl(UserRepository userRepository, AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public UniversalResponse createAssignment(CreateAssignmentRequest request, String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = modelMapper.map(request, AssignmentEntity.class);
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
            assignment.setCourse(course);

            if (LocalDateTime.now().isAfter(request.getStartDate())) {
                throw new RuntimeException("Zła data");
            }
            if (request.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Zła data");
            }

            course.getAssignments().add(assignment);

            try {
                assignmentRepository.save(assignment);
            } catch (Exception e) {
                throw new RuntimeException("Nie udało się zapisać pracy domowej do bazy danych");
            }
            return new UniversalResponse("Z powodzeniem utworzono pracę", "SUCCESS", assignment.getId());
        }
    }

    @Override
    public UniversalResponse deleteAssignment(String courseId, String assignmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignmentToDelete = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if (assignmentToDelete == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id");
            }
            try {
                assignmentRepository.delete(assignmentToDelete);
            } catch (Exception e) {
                e.printStackTrace();
                return new UniversalResponse("Operacja nie powiodła się", "ERROR");
            }
        }
        return new UniversalResponse("Z powodzeniem usunięto prace", "SUCCESS");
    }
}
