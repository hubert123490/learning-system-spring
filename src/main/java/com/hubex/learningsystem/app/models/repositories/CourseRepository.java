package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.entities.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findAll();
    List<CourseEntity> findAllByTeachersEmail(String email);
    List<CourseEntity> findAllByStudentsEmail(String email);
    @Query("SELECT c FROM CourseEntity c INNER JOIN c.teachers as t where (:name is null or c.name like :name%) and (:category is null or c.category like :category%)" +
            "and (:lastName is null or t.person.lastName like :lastName%)")
    List<CourseEntity> findAllByNameAndCategory(@Param("name") String name, @Param("category") String category, @Param("lastName") String lastName);
}
