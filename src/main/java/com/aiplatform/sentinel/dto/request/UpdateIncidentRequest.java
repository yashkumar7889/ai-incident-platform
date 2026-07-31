package com.aiplatform.sentinel.dto.request;

import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateIncidentRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Severity is mandatory")
    private Severity severity;

    @NotNull(message = "Status is mandatory")
    private Status status;
}