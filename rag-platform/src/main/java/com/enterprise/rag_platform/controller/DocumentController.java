package com.enterprise.rag_platform.controller;

import com.enterprise.rag_platform.service.DocumentService;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            documentService.ingestDocument(file);
            return ResponseEntity.ok("File uploaded, parsed, and chunks stored successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }
    }

    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam("query") String query) {
        try {
            String answer = documentService.answerWithRAG(query);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to answer query: " + e.getMessage());
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<String> getPlatformStats() {
        long totalChunks = documentService.getTotalDocumentChunksCount();
        return ResponseEntity.ok("Enterprise RAG Platform Status: ONLINE\nTotal Stored Chunks in Oracle: " + totalChunks);
    }
}