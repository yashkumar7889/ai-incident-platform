package com.aiplatform.sentinel.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;

import com.aiplatform.sentinel.entity.Incident;

public interface EmbeddingService {

    void indexIncident(Incident incident);

    List<Document> searchSimilarIncidents(String query);

    void updateIncidentEmbedding(Incident incident);

    void deleteIncidentEmbedding(UUID incidentId);

}