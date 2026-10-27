package com.enterprise.rag_platform.service;

import com.enterprise.rag_platform.model.DocumentChunk;
import com.enterprise.rag_platform.repository.DocumentChunkRepository;
import org.apache.tika.Tika;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final Tika tika = new Tika();

    @Autowired
    private DocumentChunkRepository chunkRepository;

    @Autowired
    private ChatModel chatModel;

    public String extractTextFromFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from document: " + file.getOriginalFilename(), e);
        }
    }

    public void ingestDocument(MultipartFile file) {
        String extractedText = extractTextFromFile(file);
        List<String> chunks = chunkText(extractedText, 500);

        for (String text : chunks) {
            DocumentChunk chunk = new DocumentChunk(text, file.getOriginalFilename());
            chunkRepository.save(chunk);
        }
    }

    private List<String> chunkText(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += maxLength) {
            chunks.add(text.substring(i, Math.min(length, i + maxLength)));
        }
        return chunks;
    }

    public String answerWithRAG(String query) {
        // Fetch stored chunks from Oracle database
        List<DocumentChunk> matchingChunks = chunkRepository.findAll();
        
        StringBuilder context = new StringBuilder();
        for (DocumentChunk chunk : matchingChunks) {
            context.append(chunk.getChunkText()).append("\n");
        }

        Prompt prompt = new Prompt("Context:\n" + context.toString() + "\n\nQuery: " + query);
        
        var response = chatModel.call(prompt);
        return response.getResults().get(0).getOutput().getText();
    }
    public long getTotalDocumentChunksCount() {
        return chunkRepository.count();
    }
}