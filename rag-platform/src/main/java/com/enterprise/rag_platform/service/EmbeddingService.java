package com.enterprise.rag_platform.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EmbeddingService {

    public List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            chunks.add(text.substring(start, end));
            start += (chunkSize - overlap);
        }
        return chunks;
    }

    public List<float[]> generateEmbeddings(List<String> chunks) {
        List<float[]> vectors = new ArrayList<>();
        for (String chunk : chunks) {
            float[] vector = new float[384];
            Random random = new Random(chunk.hashCode());
            for (int i = 0; i < 384; i++) {
                vector[i] = random.nextFloat();
            }
            vectors.add(vector);
        }
        return vectors;
    }

    // Generate a single embedding for a user query string
    public float[] generateQueryEmbedding(String query) {
        float[] vector = new float[384];
        Random random = new Random(query.hashCode());
        for (int i = 0; i < 384; i++) {
            vector[i] = random.nextFloat();
        }
        return vector;
    }

    // Calculate Cosine Similarity between two vectors
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}