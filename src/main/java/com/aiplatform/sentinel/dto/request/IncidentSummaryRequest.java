package com.aiplatform.sentinel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidentSummaryRequest {

    @NotBlank(message = "Description is mandatory")
    private String description;
}