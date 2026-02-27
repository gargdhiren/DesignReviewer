package com.dhiren.designeReviewer.repository;

import com.dhiren.designeReviewer.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk,Long> {
    List<DocumentChunk> findByDocumentId(Long documentId);
}
