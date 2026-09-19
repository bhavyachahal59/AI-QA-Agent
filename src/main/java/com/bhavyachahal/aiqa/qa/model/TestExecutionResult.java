package com.bhavyachahal.aiqa.qa.model;

public class TestExecutionResult {

    private String scenarioName;

    private String scenarioType;

    private String expectedOutcome;

    private int actualStatusCode;

    private String expectedStatusCode;

    private String responseBody;

    private boolean verified;

    private boolean successful;

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
        this.verified =
                expectedStatusCode != null;
        this.successful = successful;
    }

    public TestExecutionResult(
            String scenarioName,
            int actualStatusCode,
            String expectedStatusCode,
            String responseBody,
            boolean verified,
            boolean successful) {

        this.scenarioName = scenarioName;
        this.actualStatusCode = actualStatusCode;
        this.expectedStatusCode = expectedStatusCode;
        this.responseBody = responseBody;
        this.verified = verified;
        this.successful = successful;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(
            String scenarioName) {

        this.scenarioName = scenarioName;
    }

    public String getScenarioType() {
        return scenarioType;
    }

    public void setScenarioType(
            String scenarioType) {

        this.scenarioType = scenarioType;
    }

    public String getExpectedOutcome() {
        return expectedOutcome;
    }

    public void setExpectedOutcome(
            String expectedOutcome) {

        this.expectedOutcome = expectedOutcome;
    }

    public int getActualStatusCode() {
        return actualStatusCode;
    }

    public void setActualStatusCode(
            int actualStatusCode) {

        this.actualStatusCode = actualStatusCode;
    }

    public String getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(
            String expectedStatusCode) {

        this.expectedStatusCode =
                expectedStatusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(
            String responseBody) {

        this.responseBody = responseBody;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(
            boolean verified) {

        this.verified = verified;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(
            boolean successful) {

        this.successful = successful;
    }

    public String getStatus() {

        if (!verified) {
            return "UNVERIFIED";
        }

        if (successful) {
            return "PASS";
        }

        return "FAIL";
    }

    public String getSource() {

        if (scenarioType != null
                && scenarioType.startsWith("AI_")) {

            return "AI";
        }

        return "DETERMINISTIC";
    }
}