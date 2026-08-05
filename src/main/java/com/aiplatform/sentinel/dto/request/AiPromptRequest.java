package com.aiplatform.sentinel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiPromptRequest {

    @NotBlank(message = "Prompt is mandatory")
    private String prompt;
}