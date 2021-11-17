package com.hubex.learningsystem.filestorage.models.repositories;

import com.hubex.learningsystem.filestorage.models.entities.DBFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBFileRepository extends JpaRepository<DBFileEntity, String> {
}
