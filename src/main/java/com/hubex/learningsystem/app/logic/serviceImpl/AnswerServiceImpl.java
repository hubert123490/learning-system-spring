package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.AnswerService;
import com.hubex.learningsystem.app.models.dtos.AnswerDTO;
import com.hubex.learningsystem.app.models.dtos.QueryDTO;
import com.hubex.learningsystem.app.models.entities.AnswerEntity;
import com.hubex.learningsystem.app.models.entities.QuestionEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.repositories.AnswerRepository;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.QuestionRepository;
import com.hubex.learningsystem.app.models.repositories.SubmissionRepository;
import com.hubex.learningsystem.app.models.requests.AnswerRequest;
import com.hubex.learningsystem.app.models.requests.RateAnswerRequest;
import com.hubex.learningsystem.app.models.requests.SubmitAnswersRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public AnswerServiceImpl(UserRepository userRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, ExamRepository examRepository, SubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public UniversalResponse submitAnswers(String courseId, String examId, String submissionId, SubmitAnswersRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getStudentCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            SubmissionEntity submission = submissionRepository.findById(Long.valueOf(submissionId)).orElse(null);
            if(submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia(zgłoszenia) do egzaminu o podanym id");
            }
            if(submission.isClosed()) {
                throw new RuntimeException("Próba została już zamknięta");
            }
            if(submission.getStudent() != loggedUser) {
                throw new RuntimeException("Coś poszło nie tak");
            }

            for (AnswerRequest answer: request.getAnswers()) {
                QuestionEntity question = questionRepository.findById(answer.getQuestionId()).orElse(null);

                if(question == null) {
                    throw new NullPointerException("Nie znaleziono pytania o podanym id");
                }

                AnswerEntity returnValue = new AnswerEntity();
                returnValue.setSubmission(submission);
                returnValue.setQuestion(question);
                returnValue.setGivenAnswer(answer.getAnswer());
                if(question.getType().equals("radio") || question.getType().equals("text")){
                    if(question.getCorrectAnswer().equals(returnValue.getGivenAnswer())){
                        returnValue.setPoints(question.getMaxPoints());
                    }else {
                        returnValue.setPoints(0);
                    }
                    returnValue.setChecked(true);
                }
                try {
                    answerRepository.save(returnValue);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            submission.setEndDate(LocalDateTime.now());
            submission.setClosed(true);
            int maxPoints = 0;
            for (AnswerEntity answer:
                 submission.getAnswers()) {
                maxPoints += answer.getPoints();
            }
            submission.setStudentScore(maxPoints);
            submission.setGrade((double)submission.getStudentScore() / submission.getMaxScore());

            try {
                submissionRepository.save(submission);
            } catch (Exception e) {
                e.printStackTrace();
                return new UniversalResponse("Nie udało się zapisać egzaminu. Skontaktuj się z nauczycielem.","ERROR");
            }

            return new UniversalResponse("Zapisano egzamin","SUCCESS");
        }
    }

    @Override
    public List<AnswerDTO> getUncheckedAnswers(String courseId, String examId, String submissionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            SubmissionEntity submission = submissionRepository.findById(Long.valueOf(submissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia(zgłoszenia) do egzaminu o podanym id");
            }
            List<AnswerDTO> answers = answerRepository.findAllByCheckedFalseAndSubmission_Id(submission.getId()).stream()
                    .map(answer -> {
                        AnswerDTO returnValue = modelMapper.map(answer, AnswerDTO.class);
                        returnValue.setDescription(answer.getQuestion().getDescription());
                        returnValue.setMaxPoints(answer.getQuestion().getMaxPoints());
                        returnValue.setType(answer.getQuestion().getType());
                        return returnValue;
                    }).collect(Collectors.toList());

            return answers;
        }
    }

    @Override
    public UniversalResponse rateAnswer(String courseId, String examId, String submissionId, String answerId, RateAnswerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            SubmissionEntity submission = submissionRepository.findById(Long.valueOf(submissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia(zgłoszenia) do egzaminu o podanym id");
            }
            AnswerEntity answer = answerRepository.findById(Long.valueOf(answerId)).orElse(null);

            if(answer == null) {
                throw new NullPointerException("Nie znaleziono odpowiedzi o podanym id");
            }
            if(answer.getQuestion().getMaxPoints() < Integer.parseInt(request.getPoints())) {
                return new UniversalResponse("Liczba przyznanych punktów jest większa niż maksymalna liczba punktów za to pytanie", "ERROR");
            }
            if(Integer.parseInt(request.getPoints()) < 0) {
                return new UniversalResponse("Liczba przyznanych punktów jest mniejsza niż 0", "ERROR");
            }

            int points = answer.getPoints();
            answer.setPoints(Integer.parseInt(request.getPoints()));
            answer.setChecked(true);
            submission.setStudentScore(submission.getStudentScore() + answer.getPoints() - points);
            submission.setGrade((double)submission.getStudentScore() / submission.getMaxScore());

            try{
                answerRepository.save(answer);
                submissionRepository.save(submission);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return new UniversalResponse("Oceniono odpowiedź", "SUCCESS");
        }
    }

    @Override
    public List<AnswerDTO> getSubmissionAnswers(String courseId, String examId, String submissionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            SubmissionEntity submission = submissionRepository.findById(Long.valueOf(submissionId)).orElse(null);
            if (submission == null) {
                throw new NullPointerException("Nie znaleziono przystąpienia(zgłoszenia) do egzaminu o podanym id");
            }
            List<AnswerDTO> answers = answerRepository.findAllBySubmission_Id(submission.getId()).stream()
                    .map(answer -> {
                        AnswerDTO returnValue = modelMapper.map(answer, AnswerDTO.class);
                        returnValue.setDescription(answer.getQuestion().getDescription());
                        returnValue.setMaxPoints(answer.getQuestion().getMaxPoints());
                        returnValue.setType(answer.getQuestion().getType());
                        returnValue.setQueries(answer.getQuestion().getQueries().stream().map(query -> modelMapper.map(query, QueryDTO.class)).collect(Collectors.toList()));
                        returnValue.setCorrectAnswer(answer.getQuestion().getCorrectAnswer());
                        return returnValue;
                    }).collect(Collectors.toList());

            return answers;
        }
    }
}
