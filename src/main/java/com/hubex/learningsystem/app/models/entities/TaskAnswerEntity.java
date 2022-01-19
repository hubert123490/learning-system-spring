package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
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
@Table(name = "task_answers")
public class TaskAnswerEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 7939666277532704014L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int points;

    private boolean checked = false;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "task_submission_id")
    private TaskSubmissionEntity taskSubmission;

    @OneToMany(mappedBy = "taskAnswer", cascade = CascadeType.ALL)
    private Set<DBFileEntity> files = new HashSet<>();
}
