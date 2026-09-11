package com.bhavyachahal.aiqa.controller.model;

public class OpenApiExecutionRequest {

    private String baseUrl;
    private String content;
    private String format;

    public OpenApiExecutionRequest() {
    }

    public OpenApiExecutionRequest(
            String baseUrl,
            String content,
            String format) {

        this.baseUrl = baseUrl;
        this.content = content;
        this.format = format;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(
            String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(
            String content) {

        this.content = content;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(
            String format) {

        this.format = format;
    }
}