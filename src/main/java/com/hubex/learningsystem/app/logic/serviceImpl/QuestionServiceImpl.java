package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.QuestionService;
import com.hubex.learningsystem.app.models.dtos.QueryDTO;
import com.hubex.learningsystem.app.models.dtos.QuestionDTO;
import com.hubex.learningsystem.app.models.entities.*;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.QueryRepository;
import com.hubex.learningsystem.app.models.repositories.QuestionRepository;
import com.hubex.learningsystem.app.models.repositories.SubmissionRepository;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRadioRequest;
import com.hubex.learningsystem.app.models.requests.CreateQuestionRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final QueryRepository queryRepository;
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public QuestionServiceImpl(UserRepository userRepository, QuestionRepository questionRepository, ExamRepository examRepository, QueryRepository queryRepository, SubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.queryRepository = queryRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public List<QuestionDTO> getQuestions(String courseId, String examId) {
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
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id!");
            }
            List<QuestionDTO> returnValue = exam.getQuestions().stream().map(question -> {
                QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
                questionDTO.setQueries(question.getQueries().stream().map(query -> modelMapper.map(query, QueryDTO.class)).collect(Collectors.toList()));
                return questionDTO;
            }).collect(Collectors.toList());
            return returnValue;
        }
    }

    @Override
    public UniversalResponse addQuestion(String courseId, String examId, CreateQuestionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            QuestionEntity question = new QuestionEntity();
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id!");
            }
            question.setExam(exam);
            question.setDescription(request.getDescription());
            question.setType(request.getType());
            question.setMaxPoints(Integer.parseInt(request.getMaxPoints()));

            try {
                questionRepository.save(question);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono pytanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse deleteQuestion(String courseId, String examId, String questionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            QuestionEntity question = questionRepository.findById(Long.valueOf(questionId)).orElse(null);

            if (question == null) {
                throw new NullPointerException("Nie znaleziono pytania o podanym id!");
            }

            for (SubmissionEntity submission :
                    question.getExam().getSubmissions()) {
                submission.setMaxScore(submission.getMaxScore() - question.getMaxPoints());

                try {
                    submissionRepository.save(submission);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }

            try {
                questionRepository.delete(question);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return new UniversalResponse("Usunięto pytanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse addQuestionRadio(String courseId, String examId, String questionId, CreateQuestionRadioRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            QuestionEntity question = questionRepository.findById(Long.valueOf(questionId)).orElse(null);
            if(question == null) {
                throw new NullPointerException("Nie znaleziono pytania o podanym id");
            }
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id!");
            }
            if(request.getCorrectAnswer() == null || request.getCorrectAnswer().equals("")) {
                throw new RuntimeException("Prawidłowa odpowiedź nie może być pusta");
            }
            question.setCorrectAnswer(request.getCorrectAnswer());

            LinkedList<QueryEntity> queries = new LinkedList<>(question.getQueries());

            for (String query :
                    request.getQueries()) {
                QueryEntity queryEntity;
                if(queries.size() > 0)
                {
                    queryEntity = queries.pop();
                }else {
                    queryEntity = new QueryEntity();
                    queryEntity.setQuestion(question);
                }
                queryEntity.setText(query);
                try{
                    queryRepository.save(queryEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            try {
                questionRepository.save(question);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono pytanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse addQuestionText(String courseId, String examId, String questionId, String correctAnswer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            QuestionEntity question = questionRepository.findById(Long.valueOf(questionId)).orElse(null);
            if(question == null) {
                throw new NullPointerException("Nie znaleziono pytania o podanym id");
            }
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id!");
            }
            question.setCorrectAnswer(correctAnswer);

            try {
                questionRepository.save(question);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono pytanie", "SUCCESS");
        }
    }

    @Override
    public UniversalResponse addQuestionTextArea(String courseId, String examId, String questionId, String correctAnswer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            QuestionEntity question = questionRepository.findById(Long.valueOf(questionId)).orElse(null);
            if(question == null) {
                throw new NullPointerException("Nie znaleziono pytania o podanym id");
            }
            ExamEntity exam = examRepository.findById(Long.valueOf(examId)).orElse(null);
            if (exam == null) {
                throw new NullPointerException("Nie znaleziono egzaminu o podanym id!");
            }
            question.setCorrectAnswer(correctAnswer);

            try {
                questionRepository.save(question);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return new UniversalResponse("Z powodzeniem utworzono pytanie", "SUCCESS");
        }
    }
}
