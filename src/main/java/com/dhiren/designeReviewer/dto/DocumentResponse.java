package com.dhiren.designeReviewer.dto;

public class DocumentResponse {
    private Long id;
    private String title;
    private String content;

    public DocumentResponse(String title, Long id, String content) {
        this.title = title;
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
