package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAnswerRepository extends JpaRepository<TaskAnswerEntity, Long> {
    Optional<TaskAnswerEntity> findByTaskSubmissionAndTask(TaskSubmissionEntity submission, TaskEntity task);
    List<TaskAnswerEntity> findAllByTaskSubmission_Id(Long taskSubmissionId);
    List<TaskAnswerEntity> findAllByCheckedFalseAndTaskSubmission_Id(Long taskSubmissionId);
}
