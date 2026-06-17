package com.dhiren.designeReviewer.service;

import com.dhiren.designeReviewer.model.ChatMessage;
import com.dhiren.designeReviewer.model.ChatSession;
import com.dhiren.designeReviewer.repository.ChatMessageRepository;
import com.dhiren.designeReviewer.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatSession> getSessionsByDocument(Long documentId) {
        return chatSessionRepository.findByDocumentId(documentId);
    }

    public List<ChatMessage> getMessagesBySession(Long sessionId) {
        return chatMessageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
        chatSessionRepository.deleteById(sessionId);
    }

    @Transactional
    public void deleteAllSessionsForDocument(Long documentId) {
        List<ChatSession> sessions = chatSessionRepository.findByDocumentId(documentId);
        sessions.forEach(s -> chatMessageRepository.deleteBySessionId(s.getId()));
        chatSessionRepository.deleteByDocumentId(documentId);
    }
}
