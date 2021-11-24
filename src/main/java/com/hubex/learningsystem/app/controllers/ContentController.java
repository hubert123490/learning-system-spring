package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.logic.serviceImpl.ContentServiceImpl;
import com.hubex.learningsystem.app.models.dtos.ContentDTO;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses/{courseId}/lessons/{lessonId}/contents")
public class ContentController {
    private final ContentServiceImpl contentService;

    public ContentController(ContentServiceImpl contentService) {
        this.contentService = contentService;
    }

    @GetMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public List<ContentDTO> getAllContents(@PathVariable String courseId, @PathVariable String lessonId) {
        List<ContentDTO> response = contentService.getContents(courseId, lessonId);
        if (response.isEmpty()) {
            return new ArrayList<>();
        } else {
            return response;
        }
    }

    @PostMapping()
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addContent(@PathVariable String courseId, @PathVariable String lessonId, @RequestBody @Valid String title) {
        return contentService.addContent(courseId, lessonId, title);
    }

//    @PutMapping("/{contentId}")
//    @ResponseBody
//    @PreAuthorize("hasRole('TEACHER')")
//    public ContentDTO updateContent(@PathVariable String courseId, @PathVariable String lessonId, @PathVariable String contentId, @RequestBody @Valid ContentDTO request) {
//        return contentService.updateContent(courseId, lessonId, contentId, request);
//    }

    @DeleteMapping("/{contentId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse deleteContent(@PathVariable String courseId, @PathVariable String lessonId, @PathVariable String contentId) {
        return contentService.deleteContent(courseId, lessonId, contentId);
    }

    @PatchMapping("/{contentId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse addTextToContent(@PathVariable String courseId, @PathVariable String lessonId, @PathVariable String contentId,@Valid @RequestBody String textArea) {
        return contentService.updateTextarea(courseId, lessonId, contentId, textArea);
    }

    @PostMapping("{contentId}/upload-file")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse uploadFileToContent(@PathVariable String courseId, @PathVariable String lessonId, @PathVariable String contentId, @RequestParam("file") MultipartFile file) {
        return contentService.uploadFileToContent(courseId, lessonId, contentId, file);
    }

    @DeleteMapping("{contentId}/{fileId}")
    @ResponseBody
    @PreAuthorize("hasRole('TEACHER')")
    public UniversalResponse deleteFileFromContent(@PathVariable String courseId, @PathVariable String lessonId, @PathVariable String contentId, @PathVariable String fileId) {
        return contentService.deleteFileFromContent(courseId, lessonId, contentId, fileId);
    }

}
