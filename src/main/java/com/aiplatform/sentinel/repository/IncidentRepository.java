package com.aiplatform.sentinel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import com.aiplatform.sentinel.entity.Incident;

@Repository
public interface IncidentRepository
        extends JpaRepository<Incident, UUID> {

}
