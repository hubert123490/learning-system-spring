package com.hubex.learningsystem.app.logic.serviceImpl;

import com.hubex.learningsystem.app.logic.service.AnswerService;
import com.hubex.learningsystem.app.models.entities.AnswerEntity;
import com.hubex.learningsystem.app.models.entities.QuestionEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.app.models.repositories.AnswerRepository;
import com.hubex.learningsystem.app.models.repositories.ExamRepository;
import com.hubex.learningsystem.app.models.repositories.QuestionRepository;
import com.hubex.learningsystem.app.models.repositories.SubmissionRepository;
import com.hubex.learningsystem.app.models.requests.AnswerRequest;
import com.hubex.learningsystem.app.models.requests.SubmitAnswersRequest;
import com.hubex.learningsystem.app.models.responses.UniversalResponse;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SubmissionRepository submissionRepository;

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
}
