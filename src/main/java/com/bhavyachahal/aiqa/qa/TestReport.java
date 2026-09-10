package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;

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
                .filter(TestExecutionResult::isSuccessful)
                .count();
    }

    public long getFailedTests() {
        return results.stream()
                .filter(result -> !result.isSuccessful())
                .count();
    }
}