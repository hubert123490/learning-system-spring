package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.ExamEntity;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {
    List<ExamEntity> findAllByCourse_TeachersAndCourse_Id(UserEntity teacher, Long courseId);
    List<ExamEntity> findAllByCourse_Students(UserEntity student);
}
