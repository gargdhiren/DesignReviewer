package com.dhiren.designeReviewer.service;

import com.dhiren.designeReviewer.model.DocumentChunk;
import org.springframework.stereotype.Service;

import javax.naming.ldap.PagedResultsControl;
import java.util.Comparator;
import java.util.List;

@Service
public class VectorSearchService {
    private static final int TOP_K=3;
    private final EmbeddingConverter embeddingConverter;
    private final EmbeddingService embeddingService;

    public VectorSearchService(EmbeddingService embeddingService, EmbeddingConverter embeddingConverter) {
        this.embeddingService = embeddingService;
        this.embeddingConverter = embeddingConverter;
    }

    public List<Long> search(List<DocumentChunk> chunks, String question) {

        List<Double> questionVector = embeddingService.embed(question);

        return chunks.stream()

                // 1️⃣ Filter out chunks without embeddings
                .filter(chunk -> chunk.getEmbedding() != null)

                // 2️⃣ Map each chunk to score
                .map(chunk -> {
                    List<Double> chunkVector =
                            embeddingConverter.fromJson(chunk.getEmbedding());

                    double score =
                            VectorUtils.CosineSimilarity(chunkVector, questionVector);

                    return new ScoredChunk(chunk.getId(), score);
                })

                // 3️⃣ Sort by score descending
                .sorted(Comparator.comparingDouble(ScoredChunk::score).reversed())

                // 4️⃣ Take top K
                .limit(TOP_K)

                // 5️⃣ Extract IDs
                .map(ScoredChunk::chunkId)

                .toList();
    }

    private record ScoredChunk(Long chunkId, double score) {}
}
