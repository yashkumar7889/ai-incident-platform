package com.aiplatform.sentinel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolutionRequest {

    @NotBlank(message = "Description is mandatory")
    private String description;
}