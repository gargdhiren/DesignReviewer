package com.dhiren.designeReviewer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileParsingService {

    public String extractText(MultipartFile file) throws IOException {

        String contentType = file.getContentType();

        if (contentType != null && contentType.equals("application/pdf")) {
            return extractFromPdf(file);
        }

        return new String(file.getBytes());
    }

    private String extractFromPdf(MultipartFile file) throws IOException {

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}