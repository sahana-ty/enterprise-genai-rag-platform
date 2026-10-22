package com.enterprise.rag_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.enterprise.rag_platform.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        String extractedText = documentService.extractTextFromFile(file);
        
        // For now, return a success message showing the character count of extracted text
        return ResponseEntity.ok("Successfully extracted " + extractedText.length() + " characters from: " + file.getOriginalFilename());
    }
}
