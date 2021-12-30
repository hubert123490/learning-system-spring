package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.TaskSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmissionEntity, Long> {
}
