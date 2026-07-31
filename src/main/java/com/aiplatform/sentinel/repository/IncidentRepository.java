package com.aiplatform.sentinel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {

        List<Incident> findByStatus(Status status);

        List<Incident> findBySeverity(Severity severity);

        List<Incident> findBySeverityAndStatus(Severity severity, Status status);
}
