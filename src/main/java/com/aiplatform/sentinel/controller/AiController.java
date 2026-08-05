package com.aiplatform.sentinel.controller;

import com.aiplatform.sentinel.dto.request.AiPromptRequest;
import com.aiplatform.sentinel.dto.response.AiResponse;
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
}