package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
}
