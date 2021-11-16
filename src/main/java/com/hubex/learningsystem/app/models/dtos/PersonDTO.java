package com.hubex.learningsystem.app.models.dtos;

import com.hubex.learningsystem.app.models.enums.Titles;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PersonDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 8989563324362287729L;

    private Long id;
    private String firstName;
    private String lastName;
    private Titles title;

}
