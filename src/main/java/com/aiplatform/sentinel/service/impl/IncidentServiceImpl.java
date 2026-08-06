package com.aiplatform.sentinel.service.impl;

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
import com.aiplatform.sentinel.service.EmbeddingService;
import com.aiplatform.sentinel.service.IncidentService;
import com.aiplatform.sentinel.specification.IncidentSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final EmbeddingService embeddingService;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        Incident incident = incidentMapper.toEntity(request);

        incident.setStatus(Status.OPEN);

        Incident saved = incidentRepository.saveAndFlush(incident);

        embeddingService.indexIncident(saved);

        return incidentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<IncidentResponse> getIncidents(
            Severity severity,
            Status status,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Incident> specification = Specification
                .allOf(IncidentSpecification.hasSeverity(severity))
                .and(IncidentSpecification.hasStatus(status))
                .and(IncidentSpecification.hasKeyword(keyword));

        Page<Incident> incidents = incidentRepository.findAll(specification, pageable);

        return buildPageResponse(incidents);
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

    private boolean hasKeyword(String keyword) {
        return keyword != null && !keyword.isBlank();
    }

    private PageResponse<IncidentResponse> buildPageResponse(Page<Incident> incidents) {

        List<IncidentResponse> responses = incidents.getContent()
                .stream()
                .map(incidentMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                incidents.getNumber(),
                incidents.getSize(),
                incidents.getTotalElements(),
                incidents.getTotalPages(),
                incidents.isFirst(),
                incidents.isLast());
    }
}