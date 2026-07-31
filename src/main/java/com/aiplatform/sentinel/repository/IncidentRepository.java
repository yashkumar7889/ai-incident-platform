package com.aiplatform.sentinel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;

@Repository
public interface IncidentRepository extends
                JpaRepository<Incident, UUID>,
                JpaSpecificationExecutor<Incident> {

        Page<Incident> findByStatus(Status status, Pageable pageable);

        Page<Incident> findBySeverity(Severity severity, Pageable pageable);

        Page<Incident> findBySeverityAndStatus(Severity severity, Status status, Pageable pageable);

        Page<Incident> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        String title, String description, Pageable pageable);

        Page<Incident> findByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCase(
                        Status status,
                        String title,
                        Status statusAgain,
                        String description,
                        Pageable pageable);

        Page<Incident> findBySeverityAndTitleContainingIgnoreCaseOrSeverityAndDescriptionContainingIgnoreCase(
                        Severity severity,
                        String title,
                        Severity severityAgain,
                        String description,
                        Pageable pageable);

        Page<Incident> findBySeverityAndStatusAndTitleContainingIgnoreCaseOrSeverityAndStatusAndDescriptionContainingIgnoreCase(
                        Severity severity,
                        Status status,
                        String title,
                        Severity severityAgain,
                        Status statusAgain,
                        String description,
                        Pageable pageable);
}
