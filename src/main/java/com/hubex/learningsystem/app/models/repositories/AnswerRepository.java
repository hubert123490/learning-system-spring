package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findAllByCheckedFalseAndSubmission_Id(Long submissionId);
}
