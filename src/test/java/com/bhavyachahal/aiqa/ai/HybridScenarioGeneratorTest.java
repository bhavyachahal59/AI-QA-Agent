package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HybridScenarioGeneratorTest {

    @Test
    void shouldCombineDeterministicAndAiScenarios() {

        TestScenarioGenerator deterministicGenerator =
                mock(TestScenarioGenerator.class);

        AiScenarioGenerator aiScenarioGenerator =
                mock(AiScenarioGenerator.class);

        ScenarioMerger scenarioMerger =
                new ScenarioMerger();

        HybridScenarioGenerator hybridGenerator =
                new HybridScenarioGenerator(
                        deterministicGenerator,
                        aiScenarioGenerator,
                        scenarioMerger
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        TestScenario deterministicScenario =
                new TestScenario(
                        "Valid request",
                        "Verify valid request",
                        "POSITIVE"
                );

        TestScenario aiScenario =
                new TestScenario(
                        "Business rule scenario",
                        "Verify a semantic business rule",
                        "AI_NEGATIVE"
                );

        when(
                deterministicGenerator.generate(
                        endpoint
                )
        ).thenReturn(
                List.of(deterministicScenario)
        );

        when(
                aiScenarioGenerator.generateScenarios(
                        endpoint
                )
        ).thenReturn(
                List.of(aiScenario)
        );

        List<TestScenario> scenarios =
                hybridGenerator.generateScenarios(
                        endpoint
                );

        assertEquals(
                2,
                scenarios.size()
        );
    }
}