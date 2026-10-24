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
        
        // 2. Chunk text (300 characters with 30 overlap)
        List<String> chunks = embeddingService.chunkText(extractedText, 300, 30);
        
        // 3. Generate embeddings locally
        List<float[]> embeddings = embeddingService.generateEmbeddings(chunks);

        // 4. Save chunks into Oracle 23ai database
        documentService.saveChunks(chunks, file.getOriginalFilename());

        return ResponseEntity.ok("Successfully processed " + file.getOriginalFilename() + 
                " -> Extracted " + extractedText.length() + " chars, created " + 
                chunks.size() + " chunks, and saved to Oracle 23ai!");
    }
    @GetMapping("/search")
    public ResponseEntity<String> searchDocuments(@RequestParam("query") String query) {
        String relevantContext = documentService.searchRelevantContext(query);
        return ResponseEntity.ok("Best matching context found:\n\n" + relevantContext);
    }
}