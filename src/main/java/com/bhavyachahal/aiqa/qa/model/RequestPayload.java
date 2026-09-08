package com.bhavyachahal.aiqa.qa.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestPayload {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public RequestPayload() {
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void addField(String name, Object value) {
        fields.put(name, value);
    }

    public RequestPayload copy() {
        RequestPayload copy = new RequestPayload();
        copy.setFields(new LinkedHashMap<>(this.fields));
        return copy;
    }
}