package com.aiplatform.sentinel.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.entity.Incident;

import java.util.List;
import java.util.Optional;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.specification.IncidentSpecification;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class IncidentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldCreateIncidentSuccessfully() throws Exception {

        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setTitle("Database Down");
        request.setDescription("Cannot connect to PostgreSQL");
        request.setSeverity(Severity.HIGH);

        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Database Down"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    void shouldGetIncidentByIdSuccessfully() throws Exception {

        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        Incident saved = incidentRepository.save(incident);

        // Act & Assert
        mockMvc.perform(get("/api/v1/incidents/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Database Down"))
                .andExpect(jsonPath("$.data.severity").value("HIGH"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    void shouldGetAllIncidentsSuccessfully() throws Exception {

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

        // Act & Assert
        mockMvc.perform(get("/api/v1/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incidents fetched successfully"))

                .andExpect(jsonPath("$.data.content.length()").value(2))

                .andExpect(jsonPath("$.data.content[0].title").exists())
                .andExpect(jsonPath("$.data.content[0].severity").exists())
                .andExpect(jsonPath("$.data.content[0].status").exists())

                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    void shouldUpdateIncidentSuccessfully() throws Exception {

        // Arrange
        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        Incident saved = incidentRepository.save(incident);

        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setTitle("Database Restored");
        request.setDescription("Database is back online");
        request.setSeverity(Severity.LOW);
        request.setStatus(Status.RESOLVED);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/incidents/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident updated successfully"))
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Database Restored"))
                .andExpect(jsonPath("$.data.description").value("Database is back online"))
                .andExpect(jsonPath("$.data.severity").value("LOW"))
                .andExpect(jsonPath("$.data.status").value("RESOLVED"));

        // Verify database update
        Incident updated = incidentRepository.findById(saved.getId()).orElseThrow();

        assertEquals("Database Restored", updated.getTitle());
        assertEquals("Database is back online", updated.getDescription());
        assertEquals(Severity.LOW, updated.getSeverity());
        assertEquals(Status.RESOLVED, updated.getStatus());
    }

    @Test
    void shouldDeleteIncidentSuccessfully() throws Exception {

        // Arrange
        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        Incident saved = incidentRepository.save(incident);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/incidents/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident deleted successfully"));

        // Verify database
        assertFalse(incidentRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldFilterIncidentsBySeverity() throws Exception {

        // Arrange
        Incident high = new Incident();
        high.setTitle("Database Down");
        high.setDescription("PostgreSQL unavailable");
        high.setSeverity(Severity.HIGH);
        high.setStatus(Status.OPEN);

        Incident low = new Incident();
        low.setTitle("UI Alignment");
        low.setDescription("Minor CSS issue");
        low.setSeverity(Severity.LOW);
        low.setStatus(Status.OPEN);

        incidentRepository.save(high);
        incidentRepository.save(low);

        // Act & Assert
        mockMvc.perform(get("/api/v1/incidents")
                .param("severity", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Database Down"))
                .andExpect(jsonPath("$.data.content[0].severity").value("HIGH"));
    }

    @Test
    void shouldFilterIncidentsByStatus() throws Exception {

        Incident open = new Incident();
        open.setTitle("Database Down");
        open.setDescription("PostgreSQL unavailable");
        open.setSeverity(Severity.HIGH);
        open.setStatus(Status.OPEN);

        Incident resolved = new Incident();
        resolved.setTitle("CPU Spike");
        resolved.setDescription("Issue fixed");
        resolved.setSeverity(Severity.HIGH);
        resolved.setStatus(Status.RESOLVED);

        incidentRepository.save(open);
        incidentRepository.save(resolved);

        mockMvc.perform(get("/api/v1/incidents")
                .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"));
    }

    @Test
    void shouldFilterIncidentsByKeyword() throws Exception {

        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        incidentRepository.save(incident);

        mockMvc.perform(get("/api/v1/incidents")
                .param("keyword", "postgresql"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Database Down"));
    }

    @Test
    void shouldFilterIncidentsUsingCombinedSpecifications() throws Exception {

        Incident incident = new Incident();
        incident.setTitle("Database Down");
        incident.setDescription("Cannot connect to PostgreSQL");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        incidentRepository.save(incident);

        mockMvc.perform(get("/api/v1/incidents")
                .param("severity", "HIGH")
                .param("status", "OPEN")
                .param("keyword", "database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Database Down"))
                .andExpect(jsonPath("$.data.content[0].severity").value("HIGH"))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"));
    }

    @Test
    void shouldReturnPaginatedIncidents() throws Exception {

        for (int i = 1; i <= 15; i++) {
            Incident incident = new Incident();
            incident.setTitle("Incident " + i);
            incident.setDescription("Description " + i);
            incident.setSeverity(Severity.MEDIUM);
            incident.setStatus(Status.OPEN);

            incidentRepository.save(incident);
        }

        mockMvc.perform(get("/api/v1/incidents")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(5))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(15))
                .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void shouldSortIncidentsByTitleAscending() throws Exception {

        Incident b = new Incident();
        b.setTitle("Zebra");
        b.setDescription("Z");
        b.setSeverity(Severity.LOW);
        b.setStatus(Status.OPEN);

        Incident a = new Incident();
        a.setTitle("Alpha");
        a.setDescription("A");
        a.setSeverity(Severity.LOW);
        a.setStatus(Status.OPEN);

        incidentRepository.save(b);
        incidentRepository.save(a);

        mockMvc.perform(get("/api/v1/incidents")
                .param("sortBy", "title")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Alpha"))
                .andExpect(jsonPath("$.data.content[1].title").value("Zebra"));
    }

}