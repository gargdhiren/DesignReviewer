package com.dhiren.designeReviewer.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GeneratedColumn;

@Entity
public class DocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    public DocumentChunk(){}

    public DocumentChunk(Long documentId, String content) {
        this.documentId = documentId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
