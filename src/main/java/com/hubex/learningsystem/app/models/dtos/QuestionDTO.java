package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuestionDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 1552737363605250535L;

    private Long id;
    private String description;
    private String type;
    private List<QueryDTO> queries = new ArrayList<>();
    private String correctAnswer;

}
