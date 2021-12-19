package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LessonDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -406807076014172100L;

    private Long id;
    private String name;
    private String description;
    private String content;
    private List<String> fileUrls = new ArrayList<>();
}
