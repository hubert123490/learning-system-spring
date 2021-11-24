package com.hubex.learningsystem.filestorage.controllers;

import com.hubex.learningsystem.filestorage.logic.servicesImpl.DBFileServiceImpl;
import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import com.hubex.learningsystem.filestorage.models.responses.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final DBFileServiceImpl dbFileService;

    public FileController(DBFileServiceImpl dbFileService) {
        this.dbFileService = dbFileService;
    }

    @PostMapping("/upload-file")
    public UploadFileResponse uploadFile(@RequestParam("file")MultipartFile file) {
        DBFileEntity dbFile = dbFileService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(dbFile.getId())
                .toUriString();

        return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/upload-multiple-files")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        DBFileEntity dbFile = dbFileService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"" )
                .body(new ByteArrayResource(dbFile.getData()));
    }
}
