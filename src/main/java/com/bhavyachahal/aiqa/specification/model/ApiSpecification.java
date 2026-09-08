package com.bhavyachahal.aiqa.specification.model;

import java.time.Instant;
import java.util.UUID;

public class ApiSpecification {

    private UUID id;
    private String name;
    private String version;
    private String content;
    private String format;
    private Instant createdAt;

    public ApiSpecification() {
    }

    public ApiSpecification(
            UUID id,
            String name,
            String version,
            String content,
            String format,
            Instant createdAt) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.content = content;
        this.format = format;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}