package com.enterprise.rag_platform.controller;
import com.enterprise.rag_platform.service.DocumentService;
import com.enterprise.rag_platform.service.EmbeddingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmbeddingService embeddingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        // 1. Extract text using Tika
        String extractedText = documentService.extractTextFromFile(file);
        
        // 2. Chunk text (e.g., 500 characters per chunk with 50 character overlap)
        List<String> chunks = embeddingService.chunkText(extractedText, 500, 50);
        
        // 3. Generate embeddings locally via Ollama
        List<float[]> embeddings = embeddingService.generateEmbeddings(chunks);

        return ResponseEntity.ok("Successfully processed " + file.getOriginalFilename() + 
                " -> Extracted " + extractedText.length() + " chars, created " + 
                chunks.size() + " chunks, and generated " + embeddings.size() + " vector embeddings!");
    }
}