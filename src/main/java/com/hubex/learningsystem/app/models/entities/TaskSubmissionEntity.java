package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "task_submissions")
public class TaskSubmissionEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5411780374448132475L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean checkedOnce = false;

    private LocalDateTime submissionDate;

    private double grade;

    private int maxScore;

    private int studentScore;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private AssignmentEntity assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private UserEntity student;
}
