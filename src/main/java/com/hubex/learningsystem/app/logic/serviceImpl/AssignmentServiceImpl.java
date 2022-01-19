package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.AssignmentService;
import com.hubex.learningsystem.app.models.dtos.AssignmentDTO;
import com.hubex.learningsystem.app.models.dtos.ExamDTO;
import com.hubex.learningsystem.app.models.entities.*;
import com.hubex.learningsystem.app.models.repositories.AssignmentRepository;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.repositories.TaskSubmissionRepository;
import com.hubex.learningsystem.app.models.requests.ChangeDatesRequest;
import com.hubex.learningsystem.app.models.requests.CreateAssignmentRequest;
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
public class AssignmentServiceImpl implements AssignmentService {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public AssignmentServiceImpl(UserRepository userRepository, AssignmentRepository assignmentRepository, CourseRepository courseRepository, TaskSubmissionRepository taskSubmissionRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.taskSubmissionRepository = taskSubmissionRepository;
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

            for (UserEntity student :
                    course.getStudents()) {
                TaskSubmissionEntity submission = new TaskSubmissionEntity();
                submission.setAssignment(assignment);
                submission.setSubmissionDate(null);
                submission.setGrade(0);
                submission.setStudentScore(0);
                submission.setStudent(student);

                int maxScore = 0;
                for (TaskEntity task :
                        assignment.getTasks()) {
                    maxScore += task.getMaxPoints();
                }
                submission.setMaxScore(maxScore);

                try {
                    taskSubmissionRepository.save(submission);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Nie udało się zapisać przystąpienia do bazy danych");

                }
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

    @Override
    public List<AssignmentDTO> getPendingAssignments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<AssignmentDTO> assignments = assignmentRepository.findAllByCourse_Students(loggedUser).stream().filter(assignment ->
                    LocalDateTime.now().isAfter(assignment.getStartDate()) && LocalDateTime.now().isBefore(assignment.getEndDate())
            ).map(assignmentEntity -> {
                AssignmentDTO returnValue = modelMapper.map(assignmentEntity, AssignmentDTO.class);
                returnValue.setCourseId(assignmentEntity.getCourse().getId());
                return returnValue;
            }).collect(Collectors.toList());

            return assignments;
        }
    }

    @Override
    public List<AssignmentDTO> getCourseAssignments(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            List<AssignmentDTO> assignments = assignmentRepository
                    .findAllByCourse_TeachersAndCourse_Id(loggedUser, Long.valueOf(courseId))
                    .stream().map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                    .collect(Collectors.toList());
            return assignments;
        }
    }

    @Override
    public List<AssignmentDTO> getUncheckedAssignments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        } else {
            List<AssignmentEntity> assignments = assignmentRepository.findAllByCourse_Teachers(loggedUser).stream()
                    .filter(assignmentEntity -> {
                        List<TaskSubmissionEntity> submissions = assignmentEntity.getTaskSubmissions().stream()
                                .filter(taskSubmissionEntity -> taskSubmissionEntity.getTaskAnswers().stream().anyMatch(answerEntity -> !answerEntity.isChecked()))
                                .collect(Collectors.toList());
                        return !submissions.isEmpty();
                    }).filter(assignmentEntity -> LocalDateTime.now().isAfter(assignmentEntity.getEndDate())).collect(Collectors.toList());

            return assignments.stream().map(assignmentEntity -> modelMapper.map(assignmentEntity, AssignmentDTO.class)).collect(Collectors.toList());
        }
    }

    @Override
    public UniversalResponse changeAssignmentDates(String courseId, String assignmentId, ChangeDatesRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);

            if(assignment == null) {
                throw new RuntimeException("Nie znaleziono pracy o podanym id");
            }

            if (LocalDateTime.now().isAfter(request.getStartDate())) {
                throw new RuntimeException("Zła data");
            }
            if (request.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Zła data");
            }
            assignment.setStartDate(request.getStartDate());
            assignment.setEndDate(request.getEndDate());

            try {
                assignmentRepository.save(assignment);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new UniversalResponse("Z powodzeniem zmieniono datę rozpoczęcia i zakończenia", "SUCCESS", assignment.getId());
        }
    }
}
