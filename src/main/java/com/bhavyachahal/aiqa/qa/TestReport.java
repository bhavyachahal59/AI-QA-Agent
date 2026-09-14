package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestReport {

    private final List<TestExecutionResult> results =
            new ArrayList<>();

    public void addResult(TestExecutionResult result) {
        results.add(result);
    }

    public List<TestExecutionResult> getResults() {
        return results;
    }

    public int getTotalTests() {
        return results.size();
    }

    public long getPassedTests() {
        return results.stream()
                .filter(TestExecutionResult::isVerified)
                .filter(TestExecutionResult::isSuccessful)
                .count();
    }

    public long getFailedTests() {
        return results.stream()
                .filter(TestExecutionResult::isVerified)
                .filter(result -> !result.isSuccessful())
                .count();
    }

    public long getUnverifiedTests() {
        return results.stream()
                .filter(result -> !result.isVerified())
                .count();
    }

    public long getVerifiedTests() {
        return results.stream()
                .filter(TestExecutionResult::isVerified)
                .count();
    }

    public double getPassRate() {

        long verifiedTests =
                getVerifiedTests();

        if (verifiedTests == 0) {
            return 0.0;
        }

        return ((double) getPassedTests()
                / verifiedTests) * 100;
    }

    public String toSummary() {

        StringBuilder builder =
                new StringBuilder();

        builder.append("API Test Report\n");
        builder.append("---------------\n");

        builder.append("Total: ")
                .append(getTotalTests())
                .append("\n");

        builder.append("Verified: ")
                .append(getVerifiedTests())
                .append("\n");

        builder.append("Passed: ")
                .append(getPassedTests())
                .append("\n");

        builder.append("Failed: ")
                .append(getFailedTests())
                .append("\n");

        builder.append("Unverified: ")
                .append(getUnverifiedTests())
                .append("\n");

        builder.append("Pass rate: ")
                .append(String.format("%.1f", getPassRate()))
                .append("%\n");

        builder.append("\n");

        for (TestExecutionResult result : results) {

            if (!result.isVerified()) {

                builder.append("UNVERIFIED");

            } else if (result.isSuccessful()) {

                builder.append("PASS");

            } else {

                builder.append("FAIL");
            }

            builder.append(" | ")
                    .append(result.getScenarioName())
                    .append("\n");

            builder.append("Expected: ")
                    .append(result.getExpectedStatusCode())
                    .append("\n");

            builder.append("Actual: ")
                    .append(result.getActualStatusCode())
                    .append("\n");

            builder.append("\n");
        }

        return builder.toString();
    }

    public String toMarkdown() {

        StringBuilder builder =
                new StringBuilder();

        builder.append("# API Test Report\n\n");

        builder.append("## Summary\n\n");

        builder.append("- Total: ")
                .append(getTotalTests())
                .append("\n");

        builder.append("- Verified: ")
                .append(getVerifiedTests())
                .append("\n");

        builder.append("- Passed: ")
                .append(getPassedTests())
                .append("\n");

        builder.append("- Failed: ")
                .append(getFailedTests())
                .append("\n");

        builder.append("- Unverified: ")
                .append(getUnverifiedTests())
                .append("\n");

        builder.append("- Pass rate: ")
                .append(String.format("%.1f", getPassRate()))
                .append("%\n\n");

        builder.append("## Results\n\n");

        for (TestExecutionResult result : results) {

            String status;

            if (!result.isVerified()) {

                status = "UNVERIFIED";

            } else if (result.isSuccessful()) {

                status = "PASS";

            } else {

                status = "FAIL";
            }

            builder.append("### ")
                    .append(status)
                    .append(" — ")
                    .append(result.getScenarioName())
                    .append("\n\n");

            builder.append("- Expected status: ")
                    .append(result.getExpectedStatusCode())
                    .append("\n");

            builder.append("- Actual status: ")
                    .append(result.getActualStatusCode())
                    .append("\n");

            if (result.getResponseBody() != null
                    && !result.getResponseBody().isBlank()) {

                builder.append("- Response body: `")
                        .append(result.getResponseBody())
                        .append("`\n");
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    public void writeMarkdown(Path outputPath) throws IOException {

        Files.writeString(
                outputPath,
                toMarkdown()
        );
    }
}