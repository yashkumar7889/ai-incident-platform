package com.aiplatform.sentinel.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.service.EmbeddingService;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;

    public EmbeddingServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void indexIncident(Incident incident) {

        Document document = Document.builder()
                .id(incident.getId().toString())
                .text(incident.getDescription())
                .metadata("incidentId", incident.getId().toString())
                .metadata("title", incident.getTitle())
                .metadata("severity", incident.getSeverity().name())
                .metadata("status", incident.getStatus().name())
                .build();

        vectorStore.add(List.of(document));
    }

    @Override
    public List<Document> searchSimilarIncidents(String query) {

        return vectorStore.similaritySearch(query);
    }

    @Override
    public void updateIncidentEmbedding(Incident incident) {

        deleteIncidentEmbedding(incident.getId());

        indexIncident(incident);
    }   

    @Override
    public void deleteIncidentEmbedding(UUID incidentId) {

        vectorStore.delete(List.of(incidentId.toString()));
    }
}