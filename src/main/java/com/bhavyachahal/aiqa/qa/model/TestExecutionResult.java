package com.bhavyachahal.aiqa.qa.model;

public class TestExecutionResult {

    private String scenarioName;
    private int actualStatusCode;
    private String expectedStatusCode;
    private String responseBody;
    private boolean successful;

    public TestExecutionResult() {
    }

    public TestExecutionResult(
            String scenarioName,
            int actualStatusCode,
            String expectedStatusCode,
            String responseBody,
            boolean successful) {

        this.scenarioName = scenarioName;
        this.actualStatusCode = actualStatusCode;
        this.expectedStatusCode = expectedStatusCode;
        this.responseBody = responseBody;
        this.successful = successful;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public int getActualStatusCode() {
        return actualStatusCode;
    }

    public void setActualStatusCode(int actualStatusCode) {
        this.actualStatusCode = actualStatusCode;
    }

    public String getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(String expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}