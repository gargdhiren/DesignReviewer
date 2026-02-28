package com.dhiren.designeReviewer.service;

import com.dhiren.designeReviewer.dto.AnswerResponse;
import com.dhiren.designeReviewer.llm.LlmClient;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.model.DocumentChunk;
import com.dhiren.designeReviewer.repository.DesignDocumentRepository;
import com.dhiren.designeReviewer.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignDocumentService {
    private final DesignDocumentRepository repository;
    private final DocumentChunkRepository chunkRepository;
    private final TextChunkingService chunkingService;
    private final LlmClient llmClient;
    private final InMemoryVectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;

    public DesignDocumentService(DesignDocumentRepository repository,DocumentChunkRepository chunkRepository, TextChunkingService chunkingService, LlmClient llmClient, InMemoryVectorStore vectorStore, EmbeddingService embeddingService,VectorSearchService vectorSearchService) {
        this.repository = repository;
        this.llmClient = llmClient;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.vectorSearchService=vectorSearchService;
    }

    public DesignDocument createDocument(String title,String content){
        DesignDocument designDocument = repository.save(new DesignDocument(title,content));

        List<String> chunks=chunkingService.chunkText(content);

        for (String chunkText : chunks) {
            DocumentChunk chunk = chunkRepository.save(
                    new DocumentChunk(designDocument.getId(), chunkText)
            );

            List<Double> vector=embeddingService.embed(chunkText);

            vectorStore.store(chunk.getId(), vector);
        }

        return designDocument;
    }

    public List<DesignDocument> getAllDocuments(){
        return repository.findAll();
    }

    public DesignDocument getDocumentById(Long id){
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Document with id: "+id+" not found"));
    }

    public AnswerResponse askQuestion(Long documentId, String question) {
        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentId(documentId);

        List<Long> chunkIds = chunks.stream()
                .map(DocumentChunk::getId)
                .toList();

        List<Long> topChunkIds =
                vectorSearchService.search(chunkIds, question);

        String context = chunks.stream()
                .filter(c -> topChunkIds.contains(c.getId()))
                .map(DocumentChunk::getContent)
                .reduce("", (a, b) -> a + "\n\n" + b);

        String answer= llmClient.askQuestion(context, question);

        return new AnswerResponse(answer,topChunkIds);
    }
}
