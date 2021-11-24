package com.hubex.learningsystem.app.logic.service;

import com.hubex.learningsystem.app.models.dtos.ContentDTO;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContentService {
    List<ContentDTO> getContents(String courseId, String lessonId);
    UniversalResponse addContent(String courseId, String lessonId, String title);
//    ContentDTO updateContent(String courseId, String lessonId, String contentId, ContentDTO dto);
    UniversalResponse deleteContent(String courseId, String lessonId, String contentId);
    UniversalResponse updateTextarea(String courseId, String lessonId, String contentId, String textArea);
    UniversalResponse uploadFileToContent(String courseId, String lessonId, String contentId, MultipartFile file);
    UniversalResponse deleteFileFromContent(String courseId, String lessonId, String contentId, String fileId);
}
