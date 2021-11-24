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
public class ContentDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 3744976348203501909L;

    private Long id;
    private String title;
    private String value;
    private List<FileDTO> files = new ArrayList<>();
}
