package com.hubex.learningsystem.security.models.dtos;

import com.hubex.learningsystem.security.models.entities.RoleEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDTO implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 8989563324362287729L;

    private Long id;
    private String email;
    private String password;
    private Set<RoleEntity> roles = new HashSet<>();
}
