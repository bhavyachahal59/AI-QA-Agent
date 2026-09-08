package com.bhavyachahal.aiqa.specification.model;

import java.util.ArrayList;
import java.util.List;

public class ApiRequestBody {

    private String contentType;
    private String schemaType;
    private String schemaName;
    private List<ApiRequestBodyField> fields = new ArrayList<>();

    public ApiRequestBody() {
    }

    public ApiRequestBody(
            String contentType,
            String schemaType,
            String schemaName) {
        this.contentType = contentType;
        this.schemaType = schemaType;
        this.schemaName = schemaName;
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

    public List<ApiRequestBodyField> getFields() {
        return fields;
    }

    public void setFields(List<ApiRequestBodyField> fields) {
        this.fields = fields;
    }
}