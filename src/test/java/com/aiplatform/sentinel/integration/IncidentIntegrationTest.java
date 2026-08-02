package com.aiplatform.sentinel.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.aiplatform.sentinel.entity.Incident;

import java.util.List;
import java.util.Optional;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.specification.IncidentSpecification;

class IncidentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @BeforeEach
    void setUp() {
        incidentRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindIncident() {

        // Arrange
        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        // Act
        Incident saved = incidentRepository.save(incident);

        Optional<Incident> found = incidentRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Database Down", found.get().getTitle());
        assertEquals(Severity.HIGH, found.get().getSeverity());
        assertEquals(Status.OPEN, found.get().getStatus());
    }

    @Test
    void shouldFindAllIncidents() {

        // Arrange
        Incident incident1 = new Incident();
        incident1.setTitle("Database Down");
        incident1.setDescription("Cannot connect to PostgreSQL");
        incident1.setSeverity(Severity.HIGH);
        incident1.setStatus(Status.OPEN);

        Incident incident2 = new Incident();
        incident2.setTitle("CPU Spike");
        incident2.setDescription("CPU usage above 95%");
        incident2.setSeverity(Severity.MEDIUM);
        incident2.setStatus(Status.IN_PROGRESS);

        incidentRepository.save(incident1);
        incidentRepository.save(incident2);

        // Act
        List<Incident> incidents = incidentRepository.findAll();

        // Assert
        assertEquals(2, incidents.size());
    }

    @Test
    void shouldFindIncidentsBySeverity() {

        // Arrange
        Incident high = new Incident();
        high.setTitle("Database Down");
        high.setDescription("PostgreSQL unavailable");
        high.setSeverity(Severity.HIGH);
        high.setStatus(Status.OPEN);

        Incident low = new Incident();
        low.setTitle("UI Alignment");
        low.setDescription("Minor UI issue");
        low.setSeverity(Severity.LOW);
        low.setStatus(Status.OPEN);

        incidentRepository.save(high);
        incidentRepository.save(low);

        // Act
        Specification<Incident> specification = IncidentSpecification.hasSeverity(Severity.HIGH);

        List<Incident> incidents = incidentRepository.findAll(specification);

        // Assert
        assertEquals(1, incidents.size());
        assertEquals("Database Down", incidents.get(0).getTitle());
    }

    @Test
    void shouldFindIncidentsByStatus() {

        Incident open = new Incident();
        open.setTitle("Database Down");
        open.setDescription("PostgreSQL unavailable");
        open.setSeverity(Severity.HIGH);
        open.setStatus(Status.OPEN);

        Incident resolved = new Incident();
        resolved.setTitle("CPU Spike");
        resolved.setDescription("Resolved");
        resolved.setSeverity(Severity.HIGH);
        resolved.setStatus(Status.RESOLVED);

        incidentRepository.save(open);
        incidentRepository.save(resolved);

        Specification<Incident> specification = IncidentSpecification.hasStatus(Status.OPEN);

        List<Incident> incidents = incidentRepository.findAll(specification);

        assertEquals(1, incidents.size());
        assertEquals(Status.OPEN, incidents.get(0).getStatus());
    }

    @Test
    void shouldFindIncidentsByKeyword() {

        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        incidentRepository.save(incident);

        Specification<Incident> specification = IncidentSpecification.hasKeyword("postgresql");

        List<Incident> incidents = incidentRepository.findAll(specification);

        assertEquals(1, incidents.size());
    }

    @Test
    void shouldFindIncidentsUsingCombinedSpecification() {

        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        incidentRepository.save(incident);

        Specification<Incident> specification = Specification.allOf(
                IncidentSpecification.hasSeverity(Severity.HIGH),
                IncidentSpecification.hasStatus(Status.OPEN),
                IncidentSpecification.hasKeyword("database"));

        List<Incident> incidents = incidentRepository.findAll(specification);

        assertEquals(1, incidents.size());
    }

}