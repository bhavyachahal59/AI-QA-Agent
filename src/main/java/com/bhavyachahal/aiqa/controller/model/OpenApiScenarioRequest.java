package com.bhavyachahal.aiqa.controller.model;

public class OpenApiScenarioRequest {

    private String content;
    private String format;

    public OpenApiScenarioRequest() {
    }

    public OpenApiScenarioRequest(
            String content,
            String format) {

        this.content = content;
        this.format = format;
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
}