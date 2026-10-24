package com.enterprise.rag_platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chunk_seq")
    @SequenceGenerator(name = "chunk_seq", sequenceName = "DOCUMENT_CHUNK_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String chunkText;

    private String fileName;

    // Constructors
    public DocumentChunk() {}

    public DocumentChunk(String chunkText, String fileName) {
        this.chunkText = chunkText;
        this.fileName = fileName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChunkText() { return chunkText; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}