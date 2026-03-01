package com.dhiren.designeReviewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private LocalDateTime createdAt;

    public ChatSession() {}

    public ChatSession(Long documentId) {
        this.documentId = documentId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getDocumentId() { return documentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
