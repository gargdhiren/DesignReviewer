package com.dhiren.designeReviewer.service;

import com.dhiren.designeReviewer.llm.LlmClient;
import com.dhiren.designeReviewer.model.DesignDocument;
import com.dhiren.designeReviewer.repository.DesignDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignDocumentService {
    private final DesignDocumentRepository repository;
    private final LlmClient llmClient;

    public DesignDocumentService(DesignDocumentRepository repository, LlmClient llmClient) {
        this.repository = repository;
        this.llmClient = llmClient;
    }

    public DesignDocument createDocument(String title,String content){
        DesignDocument designDocument = new DesignDocument(title,content);

        return repository.save(designDocument);
    }

    public List<DesignDocument> getAllDocuments(){
        return repository.findAll();
    }

    public DesignDocument getDocumentById(Long id){
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Document with id: "+id+" not found"));
    }

    public String askQuestion(Long documentId,String question){
        DesignDocument designDocument = getDocumentById(documentId);

        return llmClient.askQuestion(designDocument.getContent(),question);
    }
}
