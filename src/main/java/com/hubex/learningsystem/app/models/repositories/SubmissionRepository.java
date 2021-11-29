package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.app.models.entities.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    SubmissionEntity findByStudent_EmailAndExam(String email, ExamEntity exam);
}
