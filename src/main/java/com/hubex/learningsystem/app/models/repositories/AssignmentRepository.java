package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.AssignmentEntity;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
    List<AssignmentEntity> findAllByCourse_Students(UserEntity user);
    List<AssignmentEntity> findAllByCourse_TeachersAndCourse_Id(UserEntity teacher, Long courseId);
    List<AssignmentEntity> findAllByCourse_Teachers(UserEntity teacher);
}
