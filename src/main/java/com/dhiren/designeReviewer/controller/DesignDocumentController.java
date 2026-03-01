package com.dhiren.designeReviewer.controller;

import com.dhiren.designeReviewer.dto.*;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.service.ChatService;
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
    private final ChatService chatService;

    public DesignDocumentController(DesignDocumentService service, FileParsingService fileParsingService, ChatService chatService) {
        this.service=service;
        this.chatService=chatService;
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
        return service.askQuestion(id,request.getQuestion(),request.getSessionId());
    }

    @GetMapping("/{documentId}/sessions")
    public List<ChatSessionResponse> getSessions(@PathVariable Long documentId) {

        return chatService.getSessionsByDocument(documentId)
                .stream()
                .map(s -> new ChatSessionResponse(
                        s.getId(),
                        s.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/sessions/{sessionId}")
    public List<ChatMessageResponse> getMessages(@PathVariable Long sessionId) {

        return chatService.getMessagesBySession(sessionId)
                .stream()
                .map(m -> new ChatMessageResponse(
                        m.getRole(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
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
