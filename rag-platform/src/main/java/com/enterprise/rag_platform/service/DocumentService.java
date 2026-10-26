package com.enterprise.rag_platform.service;

import com.enterprise.rag_platform.model.DocumentChunk;
import com.enterprise.rag_platform.repository.DocumentChunkRepository;
import org.apache.tika.Tika;
import org.springframework.ai.chat.model.ChatModel;
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

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private ChatModel chatModel; // Spring AI Chat Model for Ollama

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

    // RAG Pipeline: Retrieve context and generate AI answer
    public String answerWithRAG(String query) {
        // 1. Retrieve the best matching context chunk from Oracle
        List<DocumentChunk> allChunks = chunkRepository.findAll();
        if (allChunks.isEmpty()) {
            return "No documents found in the database to answer your question.";
        }

        float[] queryEmbedding = embeddingService.generateQueryEmbedding(query);
        String bestContext = "";
        double maxSimilarity = -1.0;

        for (DocumentChunk chunk : allChunks) {
            float[] chunkEmbedding = embeddingService.generateQueryEmbedding(chunk.getChunkText());
            double similarity = embeddingService.cosineSimilarity(queryEmbedding, chunkEmbedding);

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestContext = chunk.getChunkText();
            }
        }

        // 2. Construct the RAG Prompt for the LLM
        String prompt = "You are a helpful enterprise assistant. Use the following context to answer the user's question accurately.\n\n" +
                        "Context:\n" + bestContext + "\n\n" +
                        "Question: " + query + "\n\n" +
                        "Answer:";

        // 3. Call local Ollama model via Spring AI
        return chatModel.call(prompt);
    }
}