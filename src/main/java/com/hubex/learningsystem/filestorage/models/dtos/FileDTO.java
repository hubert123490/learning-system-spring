package com.hubex.learningsystem.filestorage.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FileDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -3687887946019691466L;

    private String id;
    private String downloadUrl;
    private String fileType;
    private String fileName;
}
