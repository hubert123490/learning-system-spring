package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
}
