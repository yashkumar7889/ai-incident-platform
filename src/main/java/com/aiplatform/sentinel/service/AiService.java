package com.aiplatform.sentinel.service;

public interface AiService {

    String chat(String prompt);

    String summarizeIncident(String description);

}