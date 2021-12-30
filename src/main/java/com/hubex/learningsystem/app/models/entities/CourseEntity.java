package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.security.models.entities.UserEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "courses")
public class CourseEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 6359288084765563865L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotBlank
    private String category;

    private String password = "";

    @ManyToMany
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> students = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "teachers_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> teachers = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<LessonEntity> lessons = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<ExamEntity> exams = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<AssignmentEntity> assignments = new HashSet<>();
}
