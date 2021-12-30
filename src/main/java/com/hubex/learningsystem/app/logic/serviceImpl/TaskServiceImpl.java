package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.TaskService;
import com.hubex.learningsystem.app.models.dtos.TaskDTO;
import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import com.hubex.learningsystem.app.models.entities.ContentEntity;
import com.hubex.learningsystem.app.models.entities.TaskEntity;
import com.hubex.learningsystem.app.models.repositories.AssignmentRepository;
import com.hubex.learningsystem.app.models.repositories.TaskRepository;
import com.hubex.learningsystem.app.models.requests.CreateTaskRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.filestorage.logic.servicesImpl.DBFileServiceImpl;
import com.hubex.learningsystem.filestorage.models.dtos.FileDTO;
import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import com.hubex.learningsystem.filestorage.models.repositories.DBFileRepository;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final TaskRepository taskRepository;
    private final DBFileServiceImpl dbFileService;
    private final DBFileRepository dbFileRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private final String fileUrl = "http://localhost:8080/api/files";

    public TaskServiceImpl(UserRepository userRepository, AssignmentRepository assignmentRepository, TaskRepository taskRepository, DBFileServiceImpl dbFileService, DBFileRepository dbFileRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.dbFileService = dbFileService;
        this.dbFileRepository = dbFileRepository;
    }

    @Override
    public List<TaskDTO> getTasks(String courseId, String assignmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))
                && loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id!");
            }
            List<TaskDTO> returnValue = assignment.getTasks().stream().map(task -> {
                TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
                taskDTO.setFiles(task.getFiles().stream().map(file -> {
                    FileDTO fileDTO = modelMapper.map(file, FileDTO.class);
                    fileDTO.setDownloadUrl(fileUrl + "/" + file.getId());
                    return fileDTO;
                }).collect(Collectors.toList()));
                taskDTO.setDescription(task.getDescription());
                return taskDTO;
            }).collect(Collectors.toList());
            return returnValue;
        }
    }

    @Override
    public UniversalResponse addTask(String courseId, String assignmentId, CreateTaskRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            TaskEntity task = new TaskEntity();
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id!");
            }
            if(request.getPoints() < 0 )
            {
                throw new RuntimeException("Liczba punktów nie może być mniejsza od 0");
            }
            task.setAssignment(assignment);
            task.setMaxPoints(request.getPoints());
            task.setTitle(request.getTitle());

            try {
                taskRepository.save(task);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono zadanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse deleteTask(String courseId, String assignmentId, String taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            TaskEntity task = taskRepository.findById(Long.valueOf(taskId)).orElse(null);

            if (task == null) {
                throw new NullPointerException("Nie znaleziono zadania o podanym id!");
            }

            try {
                taskRepository.delete(task);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return new UniversalResponse("Usunięto zadanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse updateDescription(String courseId, String assignmentId, String taskId, String description) {
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
            TaskEntity task = taskRepository.findById(Long.valueOf(taskId)).orElse(null);

            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id!");
            }
            if (task == null) {
                throw new NullPointerException("Nie znaleziono zadania o podanym id!");
            }

            if (description.isEmpty()) {
                try {
                    throw new Exception("Przesyłany opis jest pusty");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            task.setDescription(description);

            try {
                taskRepository.save(task);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Zmieniono opis zadania", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse uploadFileToTask(String courseId, String assignmentId, String taskId, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            dbFileService.storeFileTask(file, taskId);

            return new UniversalResponse("Dodano pliki", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse deleteFileFromTask(String courseId, String assignmentId, String taskId, String fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            assignmentRepository.findById(Long.valueOf(assignmentId)).orElseThrow(() ->
                    new RuntimeException("Nie znaleziono pracy o podanym id"));
            taskRepository.findById(Long.valueOf(taskId)).orElseThrow(() ->
                    new RuntimeException("Nie znaleziono zadania o podanym id"));

            DBFileEntity file = dbFileRepository.findById(fileId).orElse(null);

            if (file == null) {
                throw new NullPointerException("Nie znaleziono pliku o podanym id!");
            }
            try {
                dbFileRepository.delete(file);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Nie udało się usunąć pliku:");
            }

            return new UniversalResponse("Udało się usunąć plik", "SUCCESS");
        }
    }
}
