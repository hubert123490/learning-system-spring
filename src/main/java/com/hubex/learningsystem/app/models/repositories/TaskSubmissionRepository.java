package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import com.hubex.learningsystem.app.models.entities.TaskSubmissionEntity;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmissionEntity, Long> {
    Optional<TaskSubmissionEntity> findAllByStudentAndAssignment(UserEntity user, AssignmentEntity assignment);
}
