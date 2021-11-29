package com.hubex.learningsystem.security.models.entities;

import com.hubex.learningsystem.app.models.entities.AnswerEntity;
import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.PersonEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import com.hubex.learningsystem.security.models.enums.Roles;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity implements Serializable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -5462444259455276578L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 6, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 120)
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private PersonEntity person;

    @ManyToMany(mappedBy = "students")
    Set<CourseEntity> studentCourses = new HashSet<>();

    @ManyToMany(mappedBy = "teachers")
    Set<CourseEntity> teacherCourses = new HashSet<>();

    @OneToMany(mappedBy = "student")
    Set<SubmissionEntity> submissions;


    public UserEntity ( String email, String password) {
        this.email = email;
        this.password = password;
    }



}
