package com.dhiren.designeReviewer.repository;

import com.dhiren.designeReviewer.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk,Long> {
    List<DocumentChunk> findByDocumentId(Long documentId);

    @Modifying
    @Query("DELETE FROM DocumentChunk c WHERE c.documentId = :documentId")
    void deleteByDocumentId(Long documentId);
}
