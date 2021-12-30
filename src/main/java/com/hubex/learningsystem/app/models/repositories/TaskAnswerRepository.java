package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.TaskAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAnswerRepository extends JpaRepository<TaskAnswerEntity, Long> {
}
