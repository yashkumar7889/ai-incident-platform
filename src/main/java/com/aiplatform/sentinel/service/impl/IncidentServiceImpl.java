package com.aiplatform.sentinel.service.impl;

import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.exception.IncidentNotFoundException;
import com.aiplatform.sentinel.mapper.IncidentMapper;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        Incident incident = incidentMapper.toEntity(request);

        incident.setStatus(Status.OPEN);

        Incident saved = incidentRepository.saveAndFlush(incident);

        return incidentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponse> getIncidents(
            Severity severity,
            Status status) {

        List<Incident> incidents;

        if (severity != null && status != null) {
            incidents = incidentRepository.findBySeverityAndStatus(severity, status);
        } else if (severity != null) {
            incidents = incidentRepository.findBySeverity(severity);
        } else if (status != null) {
            incidents = incidentRepository.findByStatus(status);
        } else {
            incidents = incidentRepository.findAll();
        }

        return incidents.stream()
                .map(incidentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentResponse getIncident(UUID id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(
                        "Incident not found with id : " + id));

        return incidentMapper.toResponse(incident);
    }

    @Override
    public void deleteIncident(UUID id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(
                        "Incident not found with id : " + id));

        incidentRepository.delete(incident);
    }

    @Override
    public IncidentResponse updateIncident(UUID id, UpdateIncidentRequest request) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        incidentMapper.updateIncidentFromRequest(request, incident);
        Incident updatedIncident = incidentRepository.saveAndFlush(incident);
        return incidentMapper.toResponse(updatedIncident);
    }
}