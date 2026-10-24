package com.enterprise.rag_platform.service;

import com.enterprise.rag_platform.model.DocumentChunk;
import com.enterprise.rag_platform.repository.DocumentChunkRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class DocumentService {

    private final Tika tika = new Tika();

    @Autowired
    private DocumentChunkRepository chunkRepository;

    public String extractTextFromFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from document: " + file.getOriginalFilename(), e);
        }
    }

    public void saveChunks(List<String> chunks, String fileName) {
        for (String text : chunks) {
            DocumentChunk chunk = new DocumentChunk(text, fileName);
            chunkRepository.save(chunk);
        }
    }
}