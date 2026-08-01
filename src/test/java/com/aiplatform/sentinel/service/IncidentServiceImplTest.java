package com.aiplatform.sentinel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import com.aiplatform.sentinel.common.PageResponse;
import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.exception.IncidentNotFoundException;
import com.aiplatform.sentinel.mapper.IncidentMapper;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.service.impl.IncidentServiceImpl;

@ExtendWith(MockitoExtension.class)
class IncidentServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentMapper incidentMapper;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @Test
    void shouldCreateIncidentSuccessfully() {

        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setTitle("Database Down");
        request.setDescription("Cannot connect");
        request.setSeverity(Severity.HIGH);

        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());

        Incident saved = new Incident();
        saved.setId(UUID.randomUUID());
        saved.setTitle(request.getTitle());
        saved.setDescription(request.getDescription());
        saved.setSeverity(request.getSeverity());
        saved.setStatus(Status.OPEN);

        IncidentResponse response = new IncidentResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setStatus(saved.getStatus());

        when(incidentMapper.toEntity(request)).thenReturn(incident);
        when(incidentRepository.saveAndFlush(any(Incident.class))).thenReturn(saved);
        when(incidentMapper.toResponse(saved)).thenReturn(response);

        IncidentResponse result = incidentService.createIncident(request);

        assertNotNull(result);
        assertEquals(Status.OPEN, result.getStatus());

        ArgumentCaptor<Incident> captor = ArgumentCaptor.forClass(Incident.class);

        verify(incidentRepository).saveAndFlush(captor.capture());

        Incident captured = captor.getValue();

        assertEquals(Status.OPEN, captured.getStatus());

        verify(incidentMapper).toEntity(request);
        verify(incidentMapper).toResponse(saved);
    }

    @Test
    void shouldGetIncidentByIdSuccessfully() {

        // Arrange
        UUID id = UUID.randomUUID();

        Incident incident = new Incident();
        incident.setId(id);
        incident.setTitle("Database Down");
        incident.setStatus(Status.OPEN);

        IncidentResponse response = new IncidentResponse();
        response.setId(id);
        response.setTitle("Database Down");
        response.setStatus(Status.OPEN);

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(incident));

        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        // Act
        IncidentResponse result = incidentService.getIncident(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Database Down", result.getTitle());

        verify(incidentRepository).findById(id);
        verify(incidentMapper).toResponse(incident);
    }

    @Test
    void shouldThrowExceptionWhenIncidentNotFound() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(incidentRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act & Assert
        IncidentNotFoundException exception = assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.getIncident(id));

        assertEquals(
                "Incident not found with id : " + id,
                exception.getMessage());

        verify(incidentRepository).findById(id);
        verifyNoInteractions(incidentMapper);
    }

    @Test
    void shouldUpdateIncidentSuccessfully() {

        // Arrange
        UUID id = UUID.randomUUID();

        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setTitle("Updated Database Down");
        request.setDescription("Updated Description");
        request.setSeverity(Severity.CRITICAL);
        request.setStatus(Status.IN_PROGRESS);

        Incident incident = new Incident();
        incident.setId(id);
        incident.setTitle("Database Down");
        incident.setDescription("Old Description");
        incident.setSeverity(Severity.HIGH);
        incident.setStatus(Status.OPEN);

        IncidentResponse response = new IncidentResponse();
        response.setId(id);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setSeverity(request.getSeverity());
        response.setStatus(request.getStatus());

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.saveAndFlush(any(Incident.class)))
                .thenReturn(incident);

        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        // Act
        IncidentResponse result = incidentService.updateIncident(id, request);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertEquals("Updated Database Down", result.getTitle());

        verify(incidentRepository).findById(id);
        verify(incidentMapper).updateIncidentFromRequest(request, incident);
        verify(incidentRepository).saveAndFlush(incident);
        verify(incidentMapper).toResponse(incident);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingIncident() {

        UUID id = UUID.randomUUID();

        UpdateIncidentRequest request = new UpdateIncidentRequest();

        when(incidentRepository.findById(id))
                .thenReturn(Optional.empty());

        IncidentNotFoundException exception = assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.updateIncident(id, request));

        assertEquals(
                "Incident not found with id: " + id,
                exception.getMessage());

        verify(incidentRepository).findById(id);
        verifyNoMoreInteractions(incidentRepository);
        verifyNoInteractions(incidentMapper);
    }

    @Test
    void shouldDeleteIncidentSuccessfully() {

        // Arrange
        UUID id = UUID.randomUUID();

        Incident incident = new Incident();
        incident.setId(id);

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(incident));

        // Act
        incidentService.deleteIncident(id);

        // Assert
        verify(incidentRepository).findById(id);
        verify(incidentRepository).delete(incident);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingIncident() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(incidentRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act & Assert
        IncidentNotFoundException exception = assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.deleteIncident(id));

        assertEquals(
                "Incident not found with id : " + id,
                exception.getMessage());

        verify(incidentRepository).findById(id);
        verify(incidentRepository, never()).delete(any(Incident.class));
    }

    @Test
    void shouldGetIncidentsSuccessfully() {

        Incident incident = new Incident();
        incident.setId(UUID.randomUUID());
        incident.setTitle("Database Down");

        IncidentResponse response = new IncidentResponse();
        response.setId(incident.getId());

        Page<Incident> page = new PageImpl<>(
                List.of(incident),
                PageRequest.of(0, 10),
                1);

        when(incidentRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(page);

        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        PageResponse<IncidentResponse> result = incidentService.getIncidents(
                null,
                null,
                null,
                0,
                10,
                "createdAt",
                "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(incidentRepository)
                .findAll(any(Specification.class),
                        pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor("createdAt");

        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

}