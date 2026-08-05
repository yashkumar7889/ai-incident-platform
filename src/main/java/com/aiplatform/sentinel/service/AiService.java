package com.aiplatform.sentinel.service;

import com.aiplatform.sentinel.dto.response.AskQuestionResponse;
import com.aiplatform.sentinel.dto.response.ResolutionResponse;
import com.aiplatform.sentinel.dto.response.RootCauseResponse;
import com.aiplatform.sentinel.dto.response.SeverityPredictionResponse;

public interface AiService {

    String chat(String prompt);

    String summarizeIncident(String description);

    SeverityPredictionResponse predictSeverity(String description);

    RootCauseResponse analyzeRootCause(String description);

    ResolutionResponse recommendResolution(String description);

    AskQuestionResponse askQuestion(String question);

}