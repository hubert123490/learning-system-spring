package com.hubex.learningsystem.filestorage.logic.servicesImpl;

import com.hubex.learningsystem.app.models.entities.ContentEntity;
import com.hubex.learningsystem.app.models.entities.TaskAnswerEntity;
import com.hubex.learningsystem.app.models.entities.TaskEntity;
import com.hubex.learningsystem.app.models.repositories.ContentRepository;
import com.hubex.learningsystem.app.models.repositories.TaskAnswerRepository;
import com.hubex.learningsystem.app.models.repositories.TaskRepository;
import com.hubex.learningsystem.filestorage.exceptions.FileNotFoundException;
import com.hubex.learningsystem.filestorage.exceptions.FileStorageException;
import com.hubex.learningsystem.filestorage.logic.services.DBFileService;
import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import com.hubex.learningsystem.filestorage.models.repositories.DBFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DBFileServiceImpl implements DBFileService {
    private final DBFileRepository dbFileRepository;
    private final ContentRepository contentRepository;
    private final TaskRepository taskRepository;
    private final TaskAnswerRepository taskAnswerRepository;

    public DBFileServiceImpl(DBFileRepository dbFileRepository, ContentRepository contentRepository, TaskRepository taskRepository, TaskAnswerRepository taskAnswerRepository) {
        this.dbFileRepository = dbFileRepository;
        this.contentRepository = contentRepository;
        this.taskRepository = taskRepository;
        this.taskAnswerRepository = taskAnswerRepository;
    }


    @Override
    public DBFileEntity getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Nie znaleziono pliku o id: " + fileId));
    }

    @Override
    public DBFileEntity storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Przepraszamy! Nazwa pliku zawiera nieprawidłową sekwencję znaków " + fileName);
            }

            DBFileEntity dbFile = new DBFileEntity(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można zapisać pliku " + fileName + ". Proszę spróbować ponownie!", ex);
        }
    }

    @Override
    public DBFileEntity storeFileContent(MultipartFile file, String contentId) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Przepraszamy! Nazwa pliku zawiera nieprawidłową sekwencję znaków " + fileName);
            }

            DBFileEntity dbFile = new DBFileEntity(fileName, file.getContentType(), file.getBytes());
            ContentEntity contentEntity = contentRepository.findById(Long.valueOf(contentId)).orElse(null);

            if (contentEntity == null) {
                throw new NullPointerException("Nie znaleziono kontentu o podanym id");
            }

            dbFile.setContent(contentEntity);

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można zapisać pliku " + fileName + ". Proszę spróbować ponownie!", ex);
        }
    }



    @Override
    public DBFileEntity storeFileTask(MultipartFile file, String taskId) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Przepraszamy! Nazwa pliku zawiera nieprawidłową sekwencję znaków " + fileName);
            }

            DBFileEntity dbFile = new DBFileEntity(fileName, file.getContentType(), file.getBytes());
            TaskEntity taskEntity = taskRepository.findById(Long.valueOf(taskId)).orElse(null);

            if (taskEntity == null) {
                throw new NullPointerException("Nie znaleziono zadania o podanym id");
            }

            dbFile.setTask(taskEntity);

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można zapisać pliku " + fileName + ". Proszę spróbować ponownie!", ex);
        }
    }

    @Override
    public DBFileEntity storeFileTaskAnswer(MultipartFile file, String taskAnswerId) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Przepraszamy! Nazwa pliku zawiera nieprawidłową sekwencję znaków " + fileName);
            }

            DBFileEntity dbFile = new DBFileEntity(fileName, file.getContentType(), file.getBytes());
            TaskAnswerEntity taskAnswerEntity = taskAnswerRepository.findById(Long.valueOf(taskAnswerId)).orElse(null);

            if (taskAnswerEntity == null) {
                throw new NullPointerException("Nie znaleziono odpowiedzi o podanym id");
            }

            dbFile.setTaskAnswer(taskAnswerEntity);

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można zapisać pliku " + fileName + ". Proszę spróbować ponownie!", ex);
        }
    }
}
