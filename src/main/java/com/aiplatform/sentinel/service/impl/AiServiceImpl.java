package com.aiplatform.sentinel.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import com.aiplatform.sentinel.dto.response.ResolutionResponse;
import com.aiplatform.sentinel.dto.response.RootCauseResponse;
import com.aiplatform.sentinel.dto.response.SeverityPredictionResponse;
import com.aiplatform.sentinel.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String prompt) {

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public String summarizeIncident(String description) {

        String prompt = """
                You are an experienced Site Reliability Engineer.

                Summarize the following incident in one concise sentence.

                Incident:
                %s
                """.formatted(description);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public SeverityPredictionResponse predictSeverity(String description) {

        String prompt = """
                You are an experienced Site Reliability Engineer.

                Analyze the following incident.

                Classify the severity using ONLY one of these values:
                LOW
                MEDIUM
                HIGH
                CRITICAL

                Return ONLY valid JSON.

                Do NOT wrap the JSON in markdown.
                Do NOT use ```json.
                Do NOT explain your answer.

                Expected format:

                {
                  "severity": "...",
                  "reason": "..."
                }

                Incident:
                %s
                """.formatted(description);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        System.out.println("Raw AI Response:\n" + response);

        try {
            response = cleanJson(response);

            return objectMapper.readValue(
                    response,
                    SeverityPredictionResponse.class);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to parse AI response: " + response, ex);
        }
    }

    private String cleanJson(String response) {

        return response
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    @Override
    public RootCauseResponse analyzeRootCause(String description) {

        String prompt = """
                You are an experienced Site Reliability Engineer.

                Analyze the following production incident.

                Identify the SINGLE most probable technical root cause.

                Respond ONLY with valid JSON.

                Do NOT use markdown.
                Do NOT explain outside the JSON.

                The probableCause should be a concise but descriptive technical explanation (1–2 sentences).

                Expected format:

                {
                  "probableCause": "..."
                }

                Incident:
                %s
                """.formatted(description);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        response = cleanJson(response);

        try {
            return objectMapper.readValue(
                    response,
                    RootCauseResponse.class);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to parse AI response: " + response,
                    ex);
        }
    }

    public ResolutionResponse recommendResolution(String description) {

        String prompt = """
                You are an experienced Site Reliability Engineer.

                Analyze the following incident.

                Suggest the most appropriate resolution steps.

                Return ONLY valid JSON.

                Do NOT wrap the JSON in markdown.
                Do NOT explain outside the JSON.

                The resolution should be concise (2-4 actionable steps).

                Expected format:

                {
                  "resolution": "..."
                }

                Incident:
                %s
                """.formatted(description);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        response = cleanJson(response);

        try {
            return objectMapper.readValue(
                    response,
                    ResolutionResponse.class);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to parse AI response: " + response,
                    ex);
        }
    }
}