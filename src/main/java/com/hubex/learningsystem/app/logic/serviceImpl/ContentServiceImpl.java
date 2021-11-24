package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.ContentService;
import com.hubex.learningsystem.app.models.dtos.ContentDTO;
import com.hubex.learningsystem.app.models.entities.ContentEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import com.hubex.learningsystem.app.models.repositories.ContentRepository;
import com.hubex.learningsystem.app.models.repositories.LessonRepository;
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
public class ContentServiceImpl implements ContentService {
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final ContentRepository contentRepository;
    private final DBFileServiceImpl dbFileService;
    private final DBFileRepository dbFileRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private final String fileUrl = "http://localhost:8080/api/files";

    public ContentServiceImpl(UserRepository userRepository, LessonRepository lessonRepository, ContentRepository contentRepository, DBFileServiceImpl dbFileService, DBFileRepository dbFileRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.contentRepository = contentRepository;
        this.dbFileService = dbFileService;
        this.dbFileRepository = dbFileRepository;
    }

    @Override
    public List<ContentDTO> getContents(String courseId, String lessonId) {
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
            LessonEntity lesson = lessonRepository.findById(Long.valueOf(lessonId)).orElse(null);
            if (lesson == null) {
                throw new NullPointerException("Nie znaleziono lekcji o podanym id!");
            }
            List<ContentDTO> returnValue = lesson.getContents().stream().map(content -> {
                ContentDTO contentDTO = modelMapper.map(content, ContentDTO.class);
                contentDTO.setFiles(content.getFiles().stream().map(file -> {
                    FileDTO fileDTO = modelMapper.map(file, FileDTO.class);
                    fileDTO.setDownloadUrl(fileUrl + "/" + file.getId());
                    return fileDTO;
                }).collect(Collectors.toList()));
                contentDTO.setValue(content.getTextArea());
                return contentDTO;
            }).collect(Collectors.toList());
            return returnValue;
        }
    }

    @Override
    public UniversalResponse addContent(String courseId, String lessonId, String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ContentEntity content = new ContentEntity();
            LessonEntity lesson = lessonRepository.findById(Long.valueOf(lessonId)).orElse(null);
            if (lesson == null) {
                throw new NullPointerException("Nie znaleziono lekcji o podanym id!");
            }
            content.setLesson(lesson);
            content.setTitle(title);

            try {
                contentRepository.save(content);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono kontent", "SUCCESS");
        }
    }

//    @Override
//    public ContentDTO updateContent(String courseId, String lessonId, String contentId, ContentDTO dto) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = authentication.getName();
//
//        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);
//
//        if (loggedUser == null) {
//            throw new RuntimeException("Zaloguj się aby kontynuować");
//        }
//        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
//            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
//        } else {
//            ContentEntity content = contentRepository.findById(Long.valueOf(contentId)).orElse(null);
//            LessonEntity lesson = content.getLesson();
//
//            if (content == null) {
//                throw new NullPointerException("Nie znaleziono kontentu o podanym id!");
//            }
//            content = modelMapper.map(dto, ContentEntity.class);
//            content.setId(Long.valueOf(contentId));
//            content.setLesson(lesson);
//
//            try {
//                contentRepository.save(content);
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage());
//            }
//            return modelMapper.map(content, ContentDTO.class);
//        }
//    }

    @Override
    public UniversalResponse deleteContent(String courseId, String lessonId, String contentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ContentEntity content = contentRepository.findById(Long.valueOf(contentId)).orElse(null);

            if (content == null) {
                throw new NullPointerException("Nie znaleziono kontentu o podanym id!");
            }

            try {
                contentRepository.delete(content);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return new UniversalResponse("Usunięto kontent", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse updateTextarea(String courseId, String lessonId, String contentId, String textArea) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            ContentEntity content = contentRepository.findById(Long.valueOf(contentId)).orElse(null);

            if (content == null) {
                throw new NullPointerException("Nie znaleziono kontentu o podanym id!");
            }
            if(textArea.isEmpty()) {
                try {
                    throw new Exception("Przesyłany tekst jest pusty");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            content.setTextArea(textArea);

            try{
                contentRepository.save(content);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Zmieniono kontent", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse uploadFileToContent(String courseId, String lessonId, String contentId, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            dbFileService.storeFileContent(file, contentId);

            return new UniversalResponse("Dodano pliki", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse deleteFileFromContent(String courseId, String lessonId, String contentId, String fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            DBFileEntity file = dbFileRepository.findById(fileId).orElse(null);

            if(file == null) {
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
