package com.aiplatform.sentinel.controller;

import com.aiplatform.sentinel.dto.request.AiPromptRequest;
import com.aiplatform.sentinel.dto.request.IncidentSummaryRequest;
import com.aiplatform.sentinel.dto.request.ResolutionRequest;
import com.aiplatform.sentinel.dto.request.RootCauseRequest;
import com.aiplatform.sentinel.dto.request.SeverityPredictionRequest;
import com.aiplatform.sentinel.dto.response.AiResponse;
import com.aiplatform.sentinel.dto.response.IncidentSummaryResponse;
import com.aiplatform.sentinel.dto.response.ResolutionResponse;
import com.aiplatform.sentinel.dto.response.RootCauseResponse;
import com.aiplatform.sentinel.dto.response.SeverityPredictionResponse;
import com.aiplatform.sentinel.common.ApiResponse;
import com.aiplatform.sentinel.service.AiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "Predict Incident Severity")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Severity predicted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/severity")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SeverityPredictionResponse> predictSeverity(
            @Valid @RequestBody SeverityPredictionRequest request) {

        SeverityPredictionResponse response = aiService.predictSeverity(
                request.getDescription());

        return ApiResponse.success(
                "Severity predicted successfully",
                response);
    }

    @Operation(summary = "Analyze Incident Root Cause")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Root cause analyzed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/root-cause")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RootCauseResponse> analyzeRootCause(
            @Valid @RequestBody RootCauseRequest request) {

        RootCauseResponse response = aiService.analyzeRootCause(request.getDescription());

        return ApiResponse.success(
                "Root cause analyzed successfully",
                response);
    }

    @Operation(summary = "Recommend Incident Resolution")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resolution generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/resolution")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ResolutionResponse> recommendResolution(
            @Valid @RequestBody ResolutionRequest request) {

        ResolutionResponse response = aiService.recommendResolution(request.getDescription());

        return ApiResponse.success(
                "Resolution generated successfully",
                response);
    }
}