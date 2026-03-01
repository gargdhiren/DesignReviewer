package com.dhiren.designeReviewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private String role; // USER or ASSISTANT

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(Long sessionId, String role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
