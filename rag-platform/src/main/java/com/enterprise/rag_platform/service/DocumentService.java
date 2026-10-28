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
import java.time.LocalDateTime;
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
        List<String> chunks = chunkText(extractedText, 500, 50);
        for (String text : chunks) {
            DocumentChunk chunk = new DocumentChunk(text, file.getOriginalFilename(), LocalDateTime.now());
            chunkRepository.save(chunk);
        }
    }

    private List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        
        if (length <= chunkSize) {
            chunks.add(text);
            return chunks;
        }

        int start = 0;
        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            chunks.add(text.substring(start, end));
            
            start += (chunkSize - overlap);
            if (start >= length) {
                break;
            }
        }
        return chunks;
    }
    
    public String answerWithRAG(String query) {
        List<DocumentChunk> matchingChunks = chunkRepository.findAll();
        
        if (matchingChunks.isEmpty()) {
            return "Bello! No documents found in the database yet. Please upload a file first so I can assist you!";
        }

        StringBuilder context = new StringBuilder();
        for (DocumentChunk chunk : matchingChunks) {
            context.append(chunk.getContent()).append("\n");
        }

        String lowerQuery = query.toLowerCase();
        String contextStr = context.toString();

        if (lowerQuery.contains("skill") || lowerQuery.contains("technology") || lowerQuery.contains("languages") || lowerQuery.contains("java")) {
            return "Bello! Here are the relevant skills found in your document:\n\n" + extractRelevantSnippet(contextStr, "skill", 450);
        } else if (lowerQuery.contains("education") || lowerQuery.contains("degree") || lowerQuery.contains("college") || lowerQuery.contains("mca") || lowerQuery.contains("university")) {
            return "Bello! Here is your education background found in your document:\n\n" + extractRelevantSnippet(contextStr, "mca", 450);
        } else if (lowerQuery.contains("project") || lowerQuery.contains("internship") || lowerQuery.contains("experience")) {
            return "Bello! Here is your project/internship experience found in your document:\n\n" + extractRelevantSnippet(contextStr, "internship", 450);
        }

        Prompt prompt = new Prompt("Answer this question based only on the text below:\n\nContext:\n" + contextStr + "\n\nQuestion: " + query);
        
        var response = chatModel.call(prompt);
        String rawAnswer = response.getResults().get(0).getOutput().getText();

        if (rawAnswer.contains("successfully matched your query") || rawAnswer.isBlank()) {
            return "Bello! I checked your resume chunks. Try asking specifically about your 'skills', 'education', or 'projects'!";
        }

        return rawAnswer;
    }

    private String extractRelevantSnippet(String text, String keyword, int length) {
        int index = text.toLowerCase().indexOf(keyword);
        if (index != -1) {
            int start = Math.max(0, index - 60);
            int end = Math.min(text.length(), start + length);
            return text.substring(start, end).trim() + "...";
        }
        return text.length() > length ? text.substring(0, length) + "..." : text;
    }

    public long getTotalDocumentChunksCount() {
        return chunkRepository.count();
    }
}