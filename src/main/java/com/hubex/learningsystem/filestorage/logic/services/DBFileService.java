package com.hubex.learningsystem.filestorage.logic.services;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface DBFileService {
    DBFileEntity storeFile(MultipartFile file);
    DBFileEntity storeFileContent(MultipartFile file, String contentId);
    DBFileEntity getFile(String fileId);
    DBFileEntity storeFileTask(MultipartFile file, String taskId);
}
