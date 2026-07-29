package com.aiplatform.sentinel.service;

import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;

import java.util.List;
import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request);

    List<IncidentResponse> getAllIncidents();

    IncidentResponse getIncident(UUID id);

    void deleteIncident(UUID id);
}