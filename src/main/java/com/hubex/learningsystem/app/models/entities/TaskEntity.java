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
@Table(name = "tasks")
public class TaskEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -88904121797774130L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int maxPoints;

    @Column(columnDefinition="TEXT")
    private String description = "";

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<DBFileEntity> files = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private AssignmentEntity assignment;

}
