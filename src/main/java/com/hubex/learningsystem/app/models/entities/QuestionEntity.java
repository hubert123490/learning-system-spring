package com.hubex.learningsystem.app.models.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "questions")
public class QuestionEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -147816712845341470L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String type;

    private String correctAnswer;

    private int maxPoints;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<QueryEntity> queries = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="exam_id")
    private ExamEntity exam;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<AnswerEntity> answers;
}
