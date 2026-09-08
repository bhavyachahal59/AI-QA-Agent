package com.bhavyachahal.aiqa.specification.model;

import java.util.UUID;

public class ApiEndpoint {

    private UUID id;
    private UUID specificationId;
    private String path;
    private String method;
    private String summary;

    public ApiEndpoint() {
    }

    public ApiEndpoint(
            UUID id,
            UUID specificationId,
            String path,
            String method,
            String summary) {
        this.id = id;
        this.specificationId = specificationId;
        this.path = path;
        this.method = method;
        this.summary = summary;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(UUID specificationId) {
        this.specificationId = specificationId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}