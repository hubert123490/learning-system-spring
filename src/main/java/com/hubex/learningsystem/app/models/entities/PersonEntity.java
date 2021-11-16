package com.hubex.learningsystem.app.models.entities;

import com.hubex.learningsystem.app.models.enums.Titles;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "persons")
public class PersonEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5265361983976759302L;

    PersonEntity(String firstName, String lastName, String title, UserEntity user){
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = Titles.valueOf(title);
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 120)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 120)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Titles title;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
