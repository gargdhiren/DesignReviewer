package com.dhiren.designeReviewer.repository;

import com.dhiren.designeReviewer.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository
        extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByDocumentId(Long documentId);
}