package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

}
