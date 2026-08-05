package com.aiplatform.sentinel.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AskQuestionRequest {

    @NotBlank(message = "Question is mandatory")
    private String question;
}
