package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;

import java.util.ArrayList;
import java.util.List;

public class ApiTestService {

    private final TestScenarioGenerator scenarioGenerator;
    private final ApiTestExecutor testExecutor;

    public ApiTestService(
            TestScenarioGenerator scenarioGenerator,
            ApiTestExecutor testExecutor) {

        this.scenarioGenerator = scenarioGenerator;
        this.testExecutor = testExecutor;
    }

    public List<TestExecutionResult> executeEndpoint(
            ApiEndpoint endpoint) {

        List<TestScenario> scenarios =
                scenarioGenerator.generate(endpoint);

        List<TestExecutionResult> results =
                new ArrayList<>();

        for (TestScenario scenario : scenarios) {

            TestExecutionResult result =
                    testExecutor.execute(
                            endpoint,
                            scenario
                    );

            results.add(result);
        }

        return results;
    }
}