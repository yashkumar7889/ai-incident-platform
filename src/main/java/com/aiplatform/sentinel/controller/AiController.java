package com.aiplatform.sentinel.controller;

import com.aiplatform.sentinel.dto.request.AiPromptRequest;
import com.aiplatform.sentinel.dto.request.IncidentSummaryRequest;
import com.aiplatform.sentinel.dto.response.AiResponse;
import com.aiplatform.sentinel.dto.response.IncidentSummaryResponse;
import com.aiplatform.sentinel.common.ApiResponse;
import com.aiplatform.sentinel.service.AiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(
            @Valid @RequestBody AiPromptRequest request) {

        String response = aiService.chat(request.getPrompt());

        return ApiResponse.success(
                "AI response generated successfully",
                new AiResponse(response));
    }

    @PostMapping("/summarize")
    public ApiResponse<IncidentSummaryResponse> summarizeIncident(
            @Valid @RequestBody IncidentSummaryRequest request) {

        String summary = aiService.summarizeIncident(request.getDescription());

        return ApiResponse.success(
                "Incident summarized successfully",
                new IncidentSummaryResponse(summary));
    }
}