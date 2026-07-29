package com.aiplatform.sentinel.dto.response;

import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class IncidentResponse {

    private UUID id;
    private String title;
    private String description;
    private Severity severity;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}