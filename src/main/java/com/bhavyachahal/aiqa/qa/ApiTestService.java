package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.ai.HybridScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiTestService {

    private final HybridScenarioGenerator scenarioGenerator;
    private final ApiTestExecutor testExecutor;

    public ApiTestService(
            HybridScenarioGenerator scenarioGenerator,
            ApiTestExecutor testExecutor) {

        this.scenarioGenerator =
                scenarioGenerator;

        this.testExecutor =
                testExecutor;
    }

    public List<TestExecutionResult> executeEndpoint(
            ApiEndpoint endpoint) {

        return executeEndpoint(
                endpoint,
                null
        );
    }

    public List<TestExecutionResult> executeEndpoint(
            ApiEndpoint endpoint,
            String baseUrl) {

        List<TestScenario> scenarios =
                scenarioGenerator.generateScenarios(
                        endpoint
                );

        List<TestExecutionResult> results =
                new ArrayList<>();

        for (TestScenario scenario : scenarios) {

            if ("AI_RECOMMENDATION".equals(
                    scenario.getType())) {

                continue;
            }

            TestExecutionResult result =
                    testExecutor.execute(
                            endpoint,
                            scenario,
                            baseUrl
                    );

            results.add(
                    result
            );
        }

        return results;
    }
}