package com.dhiren.designeReviewer.service;

import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Double> embed(String text) {
        EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);
        Embedding response = embeddingModel.call(request)
                .getResults()
                .getFirst();

        return response.getOutput();
    }
}
