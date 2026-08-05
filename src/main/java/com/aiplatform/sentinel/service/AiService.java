package com.aiplatform.sentinel.service;

import com.aiplatform.sentinel.dto.response.RootCauseResponse;
import com.aiplatform.sentinel.dto.response.SeverityPredictionResponse;

public interface AiService {

    String chat(String prompt);

    String summarizeIncident(String description);

    SeverityPredictionResponse predictSeverity(String description);

    RootCauseResponse analyzeRootCause(String description);

}