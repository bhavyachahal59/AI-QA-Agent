package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.ai.HybridScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiTestServiceTest {

    @Test
    void shouldExecuteDeterministicAndAiExecutableScenariosButSkipRecommendations() {

        HybridScenarioGenerator scenarioGenerator =
                mock(HybridScenarioGenerator.class);

        ApiTestExecutor testExecutor =
                mock(ApiTestExecutor.class);

        ApiEndpoint endpoint =
                new ApiEndpoint();

        String baseUrl =
                "http://localhost:8080";

        TestScenario deterministicScenario =
                new TestScenario(
                        "Valid request",
                        "Verify valid request",
                        "POSITIVE"
                );

        deterministicScenario.setExpectedStatusCode(
                "200"
        );

        TestScenario aiExecutableScenario =
                new TestScenario(
                        "Reject transfer to same account",
                        "Source and destination must differ",
                        "AI_EXECUTABLE"
                );

        aiExecutableScenario.setExpectedOutcome(
                "REJECT"
        );

        TestScenario aiRecommendation =
                new TestScenario(
                        "Reject insufficient funds",
                        "Requires account balance setup",
                        "AI_RECOMMENDATION"
                );

        when(
                scenarioGenerator.generateScenarios(
                        endpoint
                )
        ).thenReturn(
                List.of(
                        deterministicScenario,
                        aiExecutableScenario,
                        aiRecommendation
                )
        );

        TestExecutionResult deterministicResult =
                new TestExecutionResult(
                        "Valid request",
                        200,
                        "200",
                        "",
                        true
                );

        TestExecutionResult aiExecutableResult =
                new TestExecutionResult(
                        "Reject transfer to same account",
                        400,
                        null,
                        "",
                        false
                );

        when(
                testExecutor.execute(
                        endpoint,
                        deterministicScenario,
                        baseUrl
                )
        ).thenReturn(
                deterministicResult
        );

        when(
                testExecutor.execute(
                        endpoint,
                        aiExecutableScenario,
                        baseUrl
                )
        ).thenReturn(
                aiExecutableResult
        );

        ApiTestService service =
                new ApiTestService(
                        scenarioGenerator,
                        testExecutor
                );

        List<TestExecutionResult> results =
                service.executeEndpoint(
                        endpoint,
                        baseUrl
                );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Valid request",
                results.get(0)
                        .getScenarioName()
        );

        assertEquals(
                "Reject transfer to same account",
                results.get(1)
                        .getScenarioName()
        );

        verify(
                testExecutor
        ).execute(
                endpoint,
                deterministicScenario,
                baseUrl
        );

        verify(
                testExecutor
        ).execute(
                endpoint,
                aiExecutableScenario,
                baseUrl
        );

        verify(
                testExecutor,
                never()
        ).execute(
                endpoint,
                aiRecommendation,
                baseUrl
        );
    }
}