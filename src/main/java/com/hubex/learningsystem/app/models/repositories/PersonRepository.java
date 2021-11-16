package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    PersonEntity findByUserEmail(String email);
}
