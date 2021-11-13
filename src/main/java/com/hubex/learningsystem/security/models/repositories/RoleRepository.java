package com.hubex.learningsystem.security.models.repositories;

import com.hubex.learningsystem.security.models.entities.RoleEntity;
import com.hubex.learningsystem.security.models.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(Roles name);
}
