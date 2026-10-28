package com.enterprise.rag_platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chunk_seq")
    @SequenceGenerator(name = "chunk_seq", sequenceName = "DOCUMENT_CHUNK_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CHUNK_TEXT", length = 4000)
    private String content;
    

    private String filename;

    private LocalDateTime uploadTimestamp;

    // Constructors
    public DocumentChunk() {}

    public DocumentChunk(String content, String filename, LocalDateTime uploadTimestamp) {
        this.content = content;
        this.filename = filename;
        this.uploadTimestamp = uploadTimestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public LocalDateTime getUploadTimestamp() { return uploadTimestamp; }
    public void setUploadTimestamp(LocalDateTime uploadTimestamp) { this.uploadTimestamp = uploadTimestamp; }
}