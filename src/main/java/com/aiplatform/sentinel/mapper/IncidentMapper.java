package com.aiplatform.sentinel.mapper;

import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;

public class IncidentMapper {

    private IncidentMapper() {
    }

    public static IncidentResponse toResponse(Incident incident) {

        IncidentResponse response = new IncidentResponse();

        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setStatus(incident.getStatus());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());

        return response;
    }
}