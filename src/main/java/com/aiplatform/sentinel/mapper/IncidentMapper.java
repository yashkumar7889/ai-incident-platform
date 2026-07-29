package com.aiplatform.sentinel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    IncidentResponse toResponse(Incident incident);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Incident toEntity(CreateIncidentRequest request);
}