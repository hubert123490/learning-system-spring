package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.TaskAnswerService;
import com.hubex.learningsystem.app.models.dtos.AnswerDTO;
import com.hubex.learningsystem.app.models.dtos.TaskAnswerDTO;
import com.hubex.learningsystem.app.models.entities.*;
import com.hubex.learningsystem.app.models.repositories.AssignmentRepository;
import com.hubex.learningsystem.app.models.repositories.TaskAnswerRepository;
import com.hubex.learningsystem.app.models.repositories.TaskRepository;
import com.hubex.learningsystem.app.models.repositories.TaskSubmissionRepository;
import com.hubex.learningsystem.app.models.requests.RateAnswerRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskAnswerServiceImpl implements TaskAnswerService {
    private final TaskAnswerRepository taskAnswerRepository;
    private final UserRepository userRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final TaskRepository taskRepository;
    private final DBFileServiceImpl dbFileService;
    private final DBFileRepository fileRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private final String fileUrl = "http://localhost:8080/api/files";

    public TaskAnswerServiceImpl(TaskAnswerRepository taskAnswerRepository, UserRepository userRepository, TaskSubmissionRepository taskSubmissionRepository, AssignmentRepository assignmentRepository, TaskRepository taskRepository, DBFileServiceImpl dbFileService, DBFileRepository fileRepository) {
        this.taskAnswerRepository = taskAnswerRepository;
        this.userRepository = userRepository;
        this.taskSubmissionRepository = taskSubmissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.dbFileService = dbFileService;
        this.fileRepository = fileRepository;
    }

    @Override
    public UniversalResponse addTaskAnswerFile(String courseId, String assignmentId, String taskId, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if(assignment == null) {
                throw new RuntimeException("Nie znaleziono pracy o podanym id");
            }

            TaskSubmissionEntity submissionEntity = taskSubmissionRepository.findAllByStudentAndAssignment(loggedUser, assignment).orElse(null);
            if(submissionEntity == null) {
                throw new RuntimeException("Nie znaleziono zatwierdzenia pracy");
            }else {
                if(!(LocalDateTime.now().isAfter(assignment.getStartDate()) && LocalDateTime.now().isBefore(assignment.getEndDate())))
                    throw new RuntimeException("Sprawdź czas na składanie pracy");
            }

            TaskEntity task = taskRepository.findById(Long.valueOf(taskId)).orElse(null);
            if(task == null) {
                throw new RuntimeException("Nie znaleziono zadania o podanym id");
            }

            TaskAnswerEntity answer = taskAnswerRepository.findByTaskSubmissionAndTask(submissionEntity, task).orElse(null);
            if(answer == null) {
                answer = new TaskAnswerEntity();
                answer.setTask(taskRepository.findById(Long.valueOf(taskId)).orElse(null));
                answer.setTaskSubmission(submissionEntity);
                answer.setPoints(0);

            }

            submissionEntity.setSubmissionDate(LocalDateTime.now());

            try {
                taskAnswerRepository.save(answer);
                taskSubmissionRepository.save(submissionEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }

            dbFileService.storeFileTaskAnswer(file, String.valueOf(answer.getId()));

            return new UniversalResponse("Dodano plik", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse deleteTaskAnswerFile(String courseId, String assignmentId, String taskId, String fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            AssignmentEntity assignment = assignmentRepository.findById(Long.valueOf(assignmentId)).orElse(null);
            if(assignment == null) {
                throw new RuntimeException("Nie znaleziono pracy o podanym id");
            }

            TaskSubmissionEntity submissionEntity = taskSubmissionRepository.findAllByStudentAndAssignment(loggedUser, assignment).orElse(null);
            if(submissionEntity == null) {
                throw new RuntimeException("Nie znaleziono zatwierdzenia pracy");
            }else {
                if(!(LocalDateTime.now().isAfter(assignment.getStartDate()) && LocalDateTime.now().isBefore(assignment.getEndDate())))
                    throw new RuntimeException("Sprawdź czas na składanie pracy");
            }

            TaskEntity task = taskRepository.findById(Long.valueOf(taskId)).orElse(null);
            if(task == null) {
                throw new RuntimeException("Nie znaleziono zadania o podanym id");
            }

            TaskAnswerEntity answer = taskAnswerRepository.findByTaskSubmissionAndTask(submissionEntity, task).orElse(null);

            if (answer == null) {
                throw new NullPointerException("Nie znaleziono odpowiedzi o podanym id!");
            }

            DBFileEntity fileToDelete = fileRepository.findById(fileId).orElse(null);

            if(fileToDelete == null) {
                throw new NullPointerException("Nie znaleziono pliku o podanym id");
            }

            try {
                fileRepository.delete(fileToDelete);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return new UniversalResponse("Usunięto odpowiedź", "SUCCESS");
        }
    }

    @Override
    public List<TaskAnswerDTO> getTaskSubmissionAnswers(String courseId, String assignmentId, String taskSubmissionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            TaskSubmissionEntity submission = taskSubmissionRepository.findById(Long.valueOf(taskSubmissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia do pracy o podanym id");
            }
            List<TaskAnswerDTO> answers = taskAnswerRepository.findAllByTaskSubmission_Id(submission.getId()).stream()
                    .map(answerEntity -> {
                        TaskAnswerDTO returnValue = modelMapper.map(answerEntity, TaskAnswerDTO.class);
                        returnValue.setCheckedOnce(answerEntity.getTaskSubmission().isCheckedOnce());
                        returnValue.setDescription(answerEntity.getTask().getDescription());
                        returnValue.setTitle(answerEntity.getTask().getTitle());
                        returnValue.setMaxPoints(answerEntity.getTask().getMaxPoints());
                        returnValue.setSubmissionFiles(answerEntity.getFiles().stream().map(file -> {
                            FileDTO fileDTO = modelMapper.map(file, FileDTO.class);
                            fileDTO.setDownloadUrl(fileUrl + "/" + file.getId());
                            return fileDTO;
                        }).collect(Collectors.toList()));

                        returnValue.setFiles(answerEntity.getTask().getFiles().stream().map(file -> {
                            FileDTO fileDTO = modelMapper.map(file, FileDTO.class);
                            fileDTO.setDownloadUrl(fileUrl + "/" + file.getId());
                            return fileDTO;
                        }).collect(Collectors.toList()));

                        return returnValue;
                    }).collect(Collectors.toList());

            return answers;
        }
    }

    @Override
    public UniversalResponse rateTaskAnswer(String courseId, String assignmentId, String taskSubmissionId, String taskAnswerId, RateAnswerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            TaskSubmissionEntity submission = taskSubmissionRepository.findById(Long.valueOf(taskSubmissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia do pracy o podanym id");
            }
            TaskAnswerEntity answer = taskAnswerRepository.findById(Long.valueOf(taskAnswerId)).orElse(null);

            if(answer == null) {
                throw new NullPointerException("Nie znaleziono odpowiedzi o podanym id");
            }
            if(answer.getTask().getMaxPoints() < Integer.parseInt(request.getPoints())) {
                return new UniversalResponse("Liczba przyznanych punktów jest większa niż maksymalna liczba punktów za to pytanie", "ERROR");
            }
            if(Integer.parseInt(request.getPoints()) < 0) {
                return new UniversalResponse("Liczba przyznanych punktów jest mniejsza niż 0", "ERROR");
            }

            int points = answer.getPoints();
            answer.setPoints(Integer.parseInt(request.getPoints()));
            answer.getTaskSubmission().setCheckedOnce(true);
            answer.setChecked(true);
            submission.setStudentScore(submission.getStudentScore() + answer.getPoints() - points);
            submission.setGrade((double)submission.getStudentScore() / submission.getMaxScore());

            try{
                taskAnswerRepository.save(answer);
                taskSubmissionRepository.save(submission);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return new UniversalResponse("Oceniono odpowiedź", "SUCCESS");
        }
    }

    @Override
    public List<TaskAnswerDTO> getUncheckedTaskAnswers(String courseId, String assignmentId, String taskSubmissionId) {
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
            if (assignment == null) {
                throw new NullPointerException("Nie znaleziono pracy o podanym id");
            }

            TaskSubmissionEntity submission = taskSubmissionRepository.findById(Long.valueOf(taskSubmissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia(zgłoszenia) do pracy o podanym id");
            }
            List<TaskAnswerDTO> answers = taskAnswerRepository.findAllByCheckedFalseAndTaskSubmission_Id(submission.getId()).stream()
                    .map(answer -> {
                        TaskAnswerDTO returnValue = modelMapper.map(answer, TaskAnswerDTO.class);
                        returnValue.setDescription(answer.getTask().getDescription());
                        returnValue.setMaxPoints(answer.getTask().getMaxPoints());
                        returnValue.setTitle(answer.getTask().getTitle());
                        returnValue.setFiles(answer.getTask().getFiles().stream().map(dbFileEntity -> {
                            FileDTO fileDTO = modelMapper.map(dbFileEntity, FileDTO.class);
                            fileDTO.setDownloadUrl(fileUrl + "/" + dbFileEntity.getId());
                            return fileDTO;
                        }).collect(Collectors.toList()));
                        returnValue.setSubmissionFiles(answer.getFiles().stream().map(dbFileEntity -> {
                            FileDTO fileDTO = modelMapper.map(dbFileEntity, FileDTO.class);
                            fileDTO.setDownloadUrl(fileUrl + "/" + dbFileEntity.getId());
                            return fileDTO;
                        }).collect(Collectors.toList()));
                        return returnValue;
                    }).collect(Collectors.toList());

            return answers;
        }
    }
}
