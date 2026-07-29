package com.aiplatform.sentinel.service.impl;

import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Status;
import com.aiplatform.sentinel.exception.IncidentNotFoundException;
import com.aiplatform.sentinel.mapper.IncidentMapper;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        Incident incident = new Incident();

        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setStatus(Status.OPEN);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        Incident savedIncident = incidentRepository.save(incident);

        return IncidentMapper.toResponse(savedIncident);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponse> getAllIncidents() {

        return incidentRepository.findAll()
                .stream()
                .map(IncidentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentResponse getIncident(java.util.UUID id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(
                        "Incident not found with id : " + id));

        return IncidentMapper.toResponse(incident);
    }

    @Override
    public void deleteIncident(java.util.UUID id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(
                        "Incident not found with id : " + id));

        incidentRepository.delete(incident);
    }
}