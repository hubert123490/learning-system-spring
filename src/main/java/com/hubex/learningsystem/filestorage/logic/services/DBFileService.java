package com.hubex.learningsystem.filestorage.logic.services;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface DBFileService {
    DBFileEntity storeFile(MultipartFile file, String lessonId);
    DBFileEntity getFile(String fileId);
}
