package com.hubex.learningsystem.filestorage.models.entities;

import com.hubex.learningsystem.app.models.entities.ContentEntity;
import com.hubex.learningsystem.app.models.entities.TaskAnswerEntity;
import com.hubex.learningsystem.app.models.entities.TaskEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "files")
public class DBFileEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -4375741887903625583L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name="content_id")
    private ContentEntity content;

    @ManyToOne
    @JoinColumn(name="task_id")
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name="task_answer_id")
    private TaskAnswerEntity taskAnswer;

    public DBFileEntity(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
