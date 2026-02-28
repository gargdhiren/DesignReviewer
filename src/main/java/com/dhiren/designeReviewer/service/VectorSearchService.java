package com.dhiren.designeReviewer.service;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class VectorSearchService {
    private static final int TOP_K=3;

    private final EmbeddingService embeddingService;
    private final InMemoryVectorStore vectorStore;

    public VectorSearchService(EmbeddingService embeddingService, InMemoryVectorStore vectorStore) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    public List<Long> search(List<Long> chunkIds, String question) {

        List<Double> questionVector = embeddingService.embed(question);

        return chunkIds.stream()
                .sorted(Comparator.comparingDouble(
                        id -> -VectorUtils.CosineSimilarity(
                                questionVector,
                                vectorStore.getAll().get(id)
                        )
                ))
                .limit(TOP_K)
                .toList();
    }
}
