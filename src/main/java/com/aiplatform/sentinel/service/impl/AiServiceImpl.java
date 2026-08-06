package com.aiplatform.sentinel.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import com.aiplatform.sentinel.dto.response.AskQuestionResponse;
import com.aiplatform.sentinel.dto.response.ResolutionResponse;
import com.aiplatform.sentinel.dto.response.RootCauseResponse;
import com.aiplatform.sentinel.dto.response.SeverityPredictionResponse;
import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.repository.IncidentRepository;
import com.aiplatform.sentinel.service.AiService;
import com.aiplatform.sentinel.service.EmbeddingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiServiceImpl implements AiService {

        private final ChatClient chatClient;
        private final ObjectMapper objectMapper;
        private final EmbeddingService embeddingService;

        public AiServiceImpl(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper,
                        EmbeddingService embeddingService) {
                this.chatClient = chatClientBuilder.build();
                this.objectMapper = objectMapper;
                this.embeddingService = embeddingService;
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
                                """
                                .formatted(description);

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

        @Override
        public AskQuestionResponse askQuestion(String question) {

                List<Document> documents = embeddingService.searchSimilarIncidents(question);

                String context = documents.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n"));

                String prompt = """
                                You are an experienced Site Reliability Engineer.

                                Use ONLY the incident history below to answer the user's question.

                                Incident History:
                                %s

                                User Question:
                                %s

                                Rules:
                                - Answer ONLY using the provided incident history.
                                - If multiple incidents describe the same issue, summarize them.
                                - If the answer is not present in the incident history, reply:
                                  "I couldn't find relevant incidents."

                                Answer:
                                """.formatted(context, question);

                String answer = chatClient.prompt()
                                .user(prompt)
                                .call()
                                .content();

                return new AskQuestionResponse(answer);
        }
        

        private String buildIncidentContext(List<Incident> incidents) {

                StringBuilder context = new StringBuilder();

                for (Incident incident : incidents) {

                        context.append("""
                                        Title: %s
                                        Description: %s
                                        Severity: %s
                                        Status: %s

                                        ----------------------------

                                        """.formatted(
                                        incident.getTitle(),
                                        incident.getDescription(),
                                        incident.getSeverity(),
                                        incident.getStatus()));
                }

                return context.toString();
        }
}