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

        Document document = new Document(incident.getDescription());

        document.getMetadata().put("incidentId", incident.getId().toString());
        document.getMetadata().put("title", incident.getTitle());
        document.getMetadata().put("severity", incident.getSeverity().name());
        document.getMetadata().put("status", incident.getStatus().name());

        vectorStore.add(List.of(document));
    }

    @Override
    public List<Document> searchSimilarIncidents(String query) {

        return vectorStore.similaritySearch(query);
    }
}