package com.hubex.learningsystem.app.models.dtos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnswerDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -4020141317897844838L;

    private Long id;
    private String givenAnswer;
    private boolean checked;
    private int points;
    private int maxPoints;
    private String type;
    private String description;
    private String correctAnswer;
    private List<QueryDTO> queries = new ArrayList<>();

}
