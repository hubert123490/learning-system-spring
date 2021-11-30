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
@Table(name = "exams")
public class ExamEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -6056220384401906161L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name="course_id")
    private CourseEntity course;

    @OneToMany(mappedBy = "exam")
    Set<QuestionEntity> questions = new HashSet<>();

    @OneToMany(mappedBy = "exam")
    Set<SubmissionEntity> submissions;

}