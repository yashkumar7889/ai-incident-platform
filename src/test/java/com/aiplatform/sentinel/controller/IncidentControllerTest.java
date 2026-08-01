package com.aiplatform.sentinel.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.aiplatform.sentinel.common.PageResponse;
import com.aiplatform.sentinel.config.PaginationProperties;
import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.exception.IncidentNotFoundException;
import com.aiplatform.sentinel.service.IncidentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IncidentService incidentService;

    @MockitoBean
    private PaginationProperties paginationProperties;

    @Test
    void shouldCreateIncidentSuccessfully() throws Exception {

        // Arrange
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setTitle("Database Down");
        request.setDescription("Cannot connect to PostgreSQL");
        request.setSeverity(Severity.HIGH);

        IncidentResponse response = new IncidentResponse();
        response.setId(UUID.randomUUID());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setSeverity(request.getSeverity());
        response.setStatus(Status.OPEN);

        when(incidentService.createIncident(any(CreateIncidentRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident created successfully"))
                .andExpect(jsonPath("$.data.title").value("Database Down"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));

        verify(incidentService)
                .createIncident(any(CreateIncidentRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreateIncidentRequestIsInvalid() throws Exception {

        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setDescription("Cannot connect to PostgreSQL");
        request.setSeverity(Severity.HIGH);

        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.title").exists());

        verify(incidentService, never())
                .createIncident(any(CreateIncidentRequest.class));
    }

    @Test
    void shouldGetIncidentByIdSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        IncidentResponse response = new IncidentResponse();
        response.setId(id);
        response.setTitle("Database Down");
        response.setDescription("Cannot connect to PostgreSQL");
        response.setSeverity(Severity.HIGH);
        response.setStatus(Status.OPEN);

        when(incidentService.getIncident(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/incidents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Database Down"))
                .andExpect(jsonPath("$.data.description").value("Cannot connect to PostgreSQL"))
                .andExpect(jsonPath("$.data.severity").value("HIGH"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));

        verify(incidentService).getIncident(id);
    }

    @Test
    void shouldReturnNotFoundWhenIncidentDoesNotExist() throws Exception {

        // Arrange
        UUID id = UUID.randomUUID();

        when(incidentService.getIncident(id))
                .thenThrow(new IncidentNotFoundException(
                        "Incident not found with id : " + id));

        // Act & Assert
        mockMvc.perform(get("/api/v1/incidents/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Incident not found with id : " + id))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(incidentService).getIncident(id);
    }

    @Test
    void shouldGetAllIncidentsSuccessfully() throws Exception {

        // Arrange
        IncidentResponse incident = new IncidentResponse();
        incident.setId(UUID.randomUUID());
        incident.setTitle("Database Down");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        PageResponse<IncidentResponse> pageResponse = new PageResponse<>(
                List.of(incident),
                0,
                10,
                1,
                1,
                true,
                true);

        when(incidentService.getIncidents(
                any(),
                any(),
                any(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()))
                .thenReturn(pageResponse);

        when(paginationProperties.getDefaultPage()).thenReturn(0);
        when(paginationProperties.getDefaultSize()).thenReturn(10);
        when(paginationProperties.getDefaultSortBy()).thenReturn("createdAt");
        when(paginationProperties.getDefaultSortDir()).thenReturn("desc");

        // Act & Assert
        mockMvc.perform(get("/api/v1/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incidents fetched successfully"))
                .andExpect(jsonPath("$.data.content[0].title").value("Database Down"))
                .andExpect(jsonPath("$.data.content[0].severity").value("HIGH"))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(true));

        verify(incidentService).getIncidents(
                any(),
                any(),
                any(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString());
    }

    @Test
    void shouldPassQueryParametersToService() throws Exception {

        // Arrange
        PageResponse<IncidentResponse> pageResponse = new PageResponse<>(
                List.of(),
                1,
                5,
                0,
                0,
                true,
                true);

        when(incidentService.getIncidents(
                any(),
                any(),
                any(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()))
                .thenReturn(pageResponse);

        // Act
        mockMvc.perform(get("/api/v1/incidents")
                .param("severity", "HIGH")
                .param("status", "OPEN")
                .param("keyword", "database")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "title")
                .param("sortDir", "asc"))
                .andExpect(status().isOk());

        // Assert
        verify(incidentService).getIncidents(
                Severity.HIGH,
                Status.OPEN,
                "database",
                1,
                5,
                "title",
                "asc");
    }

    @Test
    void shouldUpdateIncidentSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setTitle("Updated Database Down");
        request.setDescription("Updated description");
        request.setSeverity(Severity.CRITICAL);
        request.setStatus(Status.IN_PROGRESS);

        IncidentResponse response = new IncidentResponse();
        response.setId(id);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setSeverity(request.getSeverity());
        response.setStatus(request.getStatus());

        when(incidentService.updateIncident(eq(id), any(UpdateIncidentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/incidents/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident updated successfully"))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Updated Database Down"))
                .andExpect(jsonPath("$.data.severity").value("CRITICAL"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        verify(incidentService)
                .updateIncident(eq(id), any(UpdateIncidentRequest.class));
    }

    @Test
    void shouldDeleteIncidentSuccessfully() throws Exception {

        // Arrange
        UUID id = UUID.randomUUID();

        doNothing().when(incidentService).deleteIncident(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/incidents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(incidentService).deleteIncident(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingIncident() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new IncidentNotFoundException(
                "Incident not found with id : " + id))
                .when(incidentService)
                .deleteIncident(id);

        mockMvc.perform(delete("/api/v1/incidents/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Incident not found with id : " + id));

        verify(incidentService).deleteIncident(id);
    }

}