package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.TaskSubmissionService;
import com.hubex.learningsystem.app.models.dtos.SubmissionDTO;
import com.hubex.learningsystem.app.models.dtos.TaskSubmissionDTO;
import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.repositories.AssignmentRepository;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskSubmissionServiceImpl implements TaskSubmissionService {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final ModelMapper modelMapper = new ModelMapper();


    public TaskSubmissionServiceImpl(UserRepository userRepository, AssignmentRepository assignmentRepository) {
        this.userRepository = userRepository;

        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public List<TaskSubmissionDTO> findAllSubmissions(String courseId, String assignmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            System.out.println("error security");
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id");
            }

            List<TaskSubmissionDTO> submissions = assignment.getTaskSubmissions().stream().map(submission -> {
                TaskSubmissionDTO returnValue = modelMapper.map(submission, TaskSubmissionDTO.class);
                returnValue.setSubmissionDate(submission.getSubmissionDate());
                returnValue.setStartDate(assignment.getStartDate());
                returnValue.setEndDate(assignment.getEndDate());
                returnValue.setStudentFirstName(submission.getStudent().getPerson().getFirstName());
                returnValue.setStudentLastName(submission.getStudent().getPerson().getLastName());
                return returnValue;
            })
                    .collect(Collectors.toList());


            return submissions;
        }
    }

    @Override
    public List<TaskSubmissionDTO> findUncheckedTaskSubmissions(String courseId, String assignmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            System.out.println("error security");
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id");
            }

            List<TaskSubmissionDTO> submissions = assignment.getTaskSubmissions().stream()
                    .filter(taskSubmissionEntity -> taskSubmissionEntity.getTaskAnswers().stream().anyMatch(answerEntity -> !answerEntity.isChecked()))
                    .map(taskSubmissionEntity -> {
                        TaskSubmissionDTO returnValue = modelMapper.map(taskSubmissionEntity, TaskSubmissionDTO.class);
                        returnValue.setStudentFirstName(taskSubmissionEntity.getStudent().getPerson().getFirstName());
                        returnValue.setStudentLastName(taskSubmissionEntity.getStudent().getPerson().getLastName());
                        return returnValue;
                    })
                    .collect(Collectors.toList());


            return submissions;
        }
    }
}
