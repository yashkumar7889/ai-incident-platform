package com.aiplatform.sentinel.service;

import java.util.List;

import org.springframework.ai.document.Document;

import com.aiplatform.sentinel.entity.Incident;

public interface EmbeddingService {

    void indexIncident(Incident incident);

    List<Document> searchSimilarIncidents(String query);

}