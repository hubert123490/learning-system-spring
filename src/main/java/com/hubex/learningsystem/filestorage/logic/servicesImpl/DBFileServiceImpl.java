package com.hubex.learningsystem.filestorage.logic.servicesImpl;

import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
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
    private final LessonRepository lessonRepository;

    public DBFileServiceImpl(DBFileRepository dbFileRepository, LessonRepository lessonRepository) {
        this.dbFileRepository = dbFileRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public DBFileEntity storeFile(MultipartFile file, String lessonId) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if(fileName.contains("..")) {
                throw new FileStorageException("Przepraszamy! Nazwa pliku zawiera nieprawidłową sekwencję znaków " + fileName);
            }

            DBFileEntity dbFile = new DBFileEntity(fileName, file.getContentType(), file.getBytes());
            LessonEntity lesson = lessonRepository.findById(Long.valueOf(lessonId)).orElse(null);
            if(lesson == null) {
                throw new NullPointerException("Nie znaleziono lekcji o podanym id");
            }
            dbFile.setLesson(lesson);

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Nie można zapisać pliku " + fileName + ". Proszę spróbować ponownie!", ex);
        }
    }

    @Override
    public DBFileEntity getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Nie znaleziono pliku o id: " + fileId));
    }
}
