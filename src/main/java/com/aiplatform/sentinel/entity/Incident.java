package com.aiplatform.sentinel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    private UUID id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // getters setters
}