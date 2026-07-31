package com.aiplatform.sentinel.service;

import com.aiplatform.sentinel.common.PageResponse;
import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;

import java.util.List;
import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request);

    PageResponse<IncidentResponse> getIncidents(
            Severity severity,
            Status status,
            int page,
            int size,
            String sortBy,
            String sortDir);

    IncidentResponse getIncident(UUID id);

    void deleteIncident(UUID id);

    IncidentResponse updateIncident(UUID id, UpdateIncidentRequest request);
}