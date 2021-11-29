package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.security.models.entities.UserEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "answers")
public class AnswerEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -7171687195977492047L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    SubmissionEntity submission;

    String givenAnswer;

    int points;
}
