package com.bhavyachahal.aiqa.specification.model;

public class ApiRequestBodyField {

    private String name;
    private String type;
    private boolean required;
    private String format;

    public ApiRequestBodyField() {
    }

    public ApiRequestBodyField(
            String name,
            String type,
            boolean required,
            String format) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}