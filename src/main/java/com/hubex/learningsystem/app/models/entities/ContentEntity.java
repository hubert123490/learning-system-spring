package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contents")
public class ContentEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 7304723413258522172L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String textArea = "";

    @OneToMany(mappedBy = "content")
    private Set<DBFileEntity> files = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private LessonEntity lesson;

}
