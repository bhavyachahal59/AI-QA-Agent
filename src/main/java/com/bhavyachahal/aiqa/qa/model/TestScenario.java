package com.bhavyachahal.aiqa.qa.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestScenario {

    private String name;
    private String description;
    private String type;
    private String testData;
    private RequestPayload requestPayload;

    private Map<String, Object> parameterValues =
            new LinkedHashMap<>();

    public TestScenario() {
    }

    public TestScenario(
            String name,
            String description,
            String type) {

        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public RequestPayload getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(
            RequestPayload requestPayload) {

        this.requestPayload = requestPayload;
    }

    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    public void addParameterValue(
            String name,
            Object value) {

        parameterValues.put(
                name,
                value
        );
    }
}