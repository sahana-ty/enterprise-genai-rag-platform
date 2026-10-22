package com.enterprise.rag_platform.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class DocumentService {

    private final Tika tika = new Tika();

    public String extractTextFromFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Tika automatically detects the file type (PDF, Word, TXT) and extracts raw text
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from document: " + file.getOriginalFilename(), e);
        }
    }
}