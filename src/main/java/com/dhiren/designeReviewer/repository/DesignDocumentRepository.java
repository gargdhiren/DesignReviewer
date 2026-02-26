package com.dhiren.designeReviewer.repository;

import com.dhiren.designeReviewer.model.DesignDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignDocumentRepository extends JpaRepository<DesignDocument, Long> {
}
