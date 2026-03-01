package com.dhiren.designeReviewer.dto;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessageResponse(String role,
                               String content,
                               LocalDateTime createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getRole() { return role; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
