package com.dhiren.designeReviewer.controller;

import com.dhiren.designeReviewer.dto.CreateDocumentRequest;
import com.dhiren.designeReviewer.dto.DocumentResponse;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.service.DesignDocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
public class DesignDocumentController {
    private final DesignDocumentService service;

    public DesignDocumentController(DesignDocumentService service){
        this.service=service;
    }

    @PostMapping
    public DocumentResponse createDocument(@RequestBody CreateDocumentRequest request) {

        DesignDocument saved = service.createDocument(
                request.getTitle(),
                request.getContent()
        );

        return new DocumentResponse(
                saved.getTitle(),
                saved.getId(),

                saved.getContent()
        );
    }

    @GetMapping
    public List<DocumentResponse> getAllDocuments() {
        return service.getAllDocuments()
                .stream()
                .map(doc -> new DocumentResponse(
                        doc.getTitle(),
                        doc.getId(),
                        doc.getContent()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable Long id){
        DesignDocument document= service.getDocumentById(id);

        return new DocumentResponse(document.getTitle(), document.getId(), document.getContent());
    }
}
