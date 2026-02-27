package com.dhiren.designeReviewer.service;

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
    private static final int TOP_K = 3;

    public DesignDocumentService(DesignDocumentRepository repository,DocumentChunkRepository chunkRepository, TextChunkingService chunkingService, LlmClient llmClient) {
        this.repository = repository;
        this.llmClient = llmClient;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
    }

    public DesignDocument createDocument(String title,String content){
        DesignDocument designDocument = repository.save(new DesignDocument(title,content));

        List<String> chunks=chunkingService.chunkText(content);

        for (String chunk : chunks) {
            chunkRepository.save(
                    new DocumentChunk(designDocument.getId(), chunk)
            );
        }

        return designDocument;
    }

    public List<DesignDocument> getAllDocuments(){
        return repository.findAll();
    }

    public DesignDocument getDocumentById(Long id){
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Document with id: "+id+" not found"));
    }

    public String askQuestion(Long documentId, String question) {

        List<DocumentChunk> relevantChunks =
                getRelevantChunks(documentId, question);

        String combinedContext = relevantChunks.stream()
                .map(DocumentChunk::getContent)
                .reduce("", (a, b) -> a + "\n\n" + b);

        return llmClient.askQuestion(combinedContext, question);
    }

    private List<DocumentChunk> getRelevantChunks(Long documentId, String question) {

        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentId(documentId);

        return chunks.stream()
                .sorted((c1, c2) ->
                        Integer.compare(
                                score(c2.getContent(), question),
                                score(c1.getContent(), question)
                        )
                )
                .limit(TOP_K)
                .toList();
    }

    private int score(String chunk, String question){
        int score=0;

        String normalizedChunk = this.normalize(chunk);
        String[] questionWords = this.normalize(question).split("\\s+");

        for (String word : questionWords) {
            if (word.length() > 3 && normalizedChunk.contains(word)) {
                score++;
            }
        }

        return score;
    }

    private String normalize(String text){
        return text.toLowerCase().replaceAll("[^a-z0-9\\s]","").trim();
    }
}
