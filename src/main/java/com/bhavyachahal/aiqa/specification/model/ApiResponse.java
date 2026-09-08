package com.bhavyachahal.aiqa.specification.model;

public class ApiResponse {

    private String statusCode;
    private String description;
    private String contentType;
    private String schemaType;
    private String schemaName;

    public ApiResponse() {
    }

    public ApiResponse(
            String statusCode,
            String description,
            String contentType,
            String schemaType,
            String schemaName) {

        this.statusCode = statusCode;
        this.description = description;
        this.contentType = contentType;
        this.schemaType = schemaType;
        this.schemaName = schemaName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}