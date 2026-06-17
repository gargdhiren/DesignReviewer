package com.dhiren.designeReviewer.controller;

import com.dhiren.designeReviewer.dto.*;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.service.ChatService;
import com.dhiren.designeReviewer.service.DesignDocumentService;
import com.dhiren.designeReviewer.service.FileParsingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
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
                        null  // don't send full content in list — too much data
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable Long id){
        DesignDocument document = service.getDocumentById(id);
        String content = document.getContent();
        String preview = content != null && content.length() > 500
                ? content.substring(0, 500) + "..."
                : content;
        return new DocumentResponse(document.getTitle(), document.getId(), preview);
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
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();

        DesignDocument saved = service.createDocument(title, content, fileData, fileName);

        return new DocumentResponse(saved.getTitle(), saved.getId(), null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        service.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DesignDocument document = service.getDocumentById(id);

        if (document.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = document.getFileName() != null ? document.getFileName() : "document.pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(document.getFileData());
    }
}
