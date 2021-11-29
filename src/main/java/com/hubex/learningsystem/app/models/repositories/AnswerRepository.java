package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
}
