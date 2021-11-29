package com.hubex.learningsystem.app.models.repositories;

import com.hubex.learningsystem.app.models.entities.QueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepository<QueryEntity, Long> {

}
