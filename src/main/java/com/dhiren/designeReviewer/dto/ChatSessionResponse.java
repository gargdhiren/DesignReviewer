package com.dhiren.designeReviewer.dto;

import java.time.LocalDateTime;

public class ChatSessionResponse {

    private Long id;
    private LocalDateTime createdAt;

    public ChatSessionResponse(Long id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
