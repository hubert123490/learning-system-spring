package com.hubex.learningsystem.app.models.dtos;

import com.hubex.learningsystem.filestorage.models.dtos.FileDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 133172614385835881L;

    private Long id;
    private String title;
    private String description;
    private List<FileDTO> files = new ArrayList<>();
}
