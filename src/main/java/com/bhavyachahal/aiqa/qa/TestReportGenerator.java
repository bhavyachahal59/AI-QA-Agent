package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;

import java.util.List;

public class TestReportGenerator {

    private final ApiTestExecutor executor;

    public TestReportGenerator(ApiTestExecutor executor) {
        this.executor = executor;
    }

    public TestReport executeAll(
            ApiEndpoint endpoint,
            List<TestScenario> scenarios) {

        TestReport report = new TestReport();

        for (TestScenario scenario : scenarios) {

            TestExecutionResult result =
                    executor.execute(
                            endpoint,
                            scenario
                    );

            report.addResult(result);
        }

        return report;
    }
}