package com.dhiren.designeReviewer.dto;

public class CreateDocumentRequest {
    public String title;
    public String content;

    public CreateDocumentRequest(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
