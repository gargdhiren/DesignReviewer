package com.dhiren.designeReviewer.controller;

import com.dhiren.designeReviewer.dto.AnswerResponse;
import com.dhiren.designeReviewer.dto.AskQuestionRequest;
import com.dhiren.designeReviewer.dto.CreateDocumentRequest;
import com.dhiren.designeReviewer.dto.DocumentResponse;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.service.DesignDocumentService;
import com.dhiren.designeReviewer.service.FileParsingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
public class DesignDocumentController {
    private final DesignDocumentService service;
    private final FileParsingService fileParsingService;

    public DesignDocumentController(DesignDocumentService service, FileParsingService fileParsingService) {
        this.service=service;
        this.fileParsingService=fileParsingService;
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

    @PostMapping("/{id}/ask")
    public AnswerResponse askQuestion(@PathVariable Long id, @RequestBody AskQuestionRequest request){
        return service.askQuestion(id,request.getQuestion());
    }

    @PostMapping(value="/upload", consumes ="multipart/form-data")
    public DocumentResponse uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) throws Exception {

        String content = fileParsingService.extractText(file);

        DesignDocument saved =
                service.createDocument(title, content);

        return new DocumentResponse(
                saved.getTitle(),
                saved.getId(),
                saved.getContent()
        );
    }
}
