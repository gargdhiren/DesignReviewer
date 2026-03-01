package com.dhiren.designeReviewer.service;

import com.dhiren.designeReviewer.dto.AnswerResponse;
import com.dhiren.designeReviewer.llm.LlmClient;
import com.dhiren.designeReviewer.model.ChatMessage;
import com.dhiren.designeReviewer.model.ChatSession;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.model.DocumentChunk;
import com.dhiren.designeReviewer.repository.ChatMessageRepository;
import com.dhiren.designeReviewer.repository.ChatSessionRepository;
import com.dhiren.designeReviewer.repository.DesignDocumentRepository;
import com.dhiren.designeReviewer.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesignDocumentService {
    private final DesignDocumentRepository repository;
    private final DocumentChunkRepository chunkRepository;
    private final TextChunkingService chunkingService;
    private final LlmClient llmClient;
    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final EmbeddingConverter embeddingConverter;

    public DesignDocumentService(DesignDocumentRepository repository,DocumentChunkRepository chunkRepository, TextChunkingService chunkingService, LlmClient llmClient, EmbeddingService embeddingService,VectorSearchService vectorSearchService,ChatMessageRepository chatMessageRepository,ChatSessionRepository chatSessionRepository, EmbeddingConverter embeddingConverter) {
        this.repository = repository;
        this.llmClient = llmClient;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
        this.vectorSearchService=vectorSearchService;
        this.chatMessageRepository=chatMessageRepository;
        this.chatSessionRepository=chatSessionRepository;
        this.embeddingConverter=embeddingConverter;
    }

    public DesignDocument createDocument(String title,String content){
        DesignDocument designDocument = repository.save(new DesignDocument(title,content));

        List<String> chunks=chunkingService.chunkText(content);

        for (String chunkText : chunks) {
            List<Double> vector=embeddingService.embed(chunkText);

            String embeddingJson =
                    embeddingConverter.toJson(vector);

            DocumentChunk chunk = new DocumentChunk(
                    designDocument.getId(),
                    chunkText
            );

            chunk.setEmbedding(embeddingJson);

            // 4️⃣ Save chunk (embedding stored in DB)
            chunkRepository.save(chunk);
        }

        return designDocument;
    }

    public List<DesignDocument> getAllDocuments(){
        return repository.findAll();
    }

    public DesignDocument getDocumentById(Long id){
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Document with id: "+id+" not found"));
    }

    @Transactional
    public AnswerResponse askQuestion(Long documentId,
                                      String question,
                                      Long sessionId) {

        ChatSession session;

        if (sessionId == null) {
            session = chatSessionRepository.save(
                    new ChatSession(documentId)
            );
        } else {
            session = chatSessionRepository.findById(sessionId)
                    .orElseThrow();
        }

        // Save user message
        chatMessageRepository.save(
                new ChatMessage(session.getId(), "USER", question)
        );

        // Retrieve context using vector search
        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentId(documentId);

        List<Long> chunkIds = chunks.stream()
                .map(DocumentChunk::getId)
                .toList();

        List<Long> topChunkIds =
                vectorSearchService.search(chunks, question);

        String context = chunks.stream()
                .filter(c -> topChunkIds.contains(c.getId()))
                .map(c -> "[Chunk " + c.getId() + "]\n" + c.getContent())
                .reduce("", (a, b) -> a + "\n\n" + b);

        String answer = llmClient.askQuestion(context, question);

        // Save assistant message
        chatMessageRepository.save(
                new ChatMessage(session.getId(), "ASSISTANT", answer)
        );

        return new AnswerResponse(answer, topChunkIds,session.getId());
    }
}
