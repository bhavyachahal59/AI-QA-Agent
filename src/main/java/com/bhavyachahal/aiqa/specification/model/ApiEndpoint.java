package com.bhavyachahal.aiqa.specification.model;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class ApiEndpoint {

    private UUID id;
    private UUID specificationId;
    private String path;
    private String method;
    private String summary;
    private List<ApiParameter> parameters = new ArrayList<>();
    private ApiRequestBody requestBody;
    private List<ApiResponse> responses = new ArrayList<>();

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

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }

    public ApiRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(ApiRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public List<ApiResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<ApiResponse> responses) {
        this.responses = responses;
    }
}