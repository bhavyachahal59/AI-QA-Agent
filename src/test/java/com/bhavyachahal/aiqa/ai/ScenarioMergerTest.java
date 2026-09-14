package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScenarioMergerTest {

    private final ScenarioMerger merger =
            new ScenarioMerger();

    @Test
    void shouldMergeDeterministicAndAiScenarios() {

        TestScenario deterministic =
                new TestScenario(
                        "Valid request",
                        "Verify valid request",
                        "POSITIVE"
                );

        TestScenario ai =
                new TestScenario(
                        "Insufficient balance",
                        "Verify insufficient balance handling",
                        "AI_NEGATIVE"
                );

        List<TestScenario> merged =
                merger.merge(
                        List.of(deterministic),
                        List.of(ai)
                );

        assertEquals(
                2,
                merged.size()
        );
    }

    @Test
    void shouldRemoveDuplicateScenarios() {

        TestScenario deterministic =
                new TestScenario(
                        "Valid request",
                        "Verify valid request",
                        "POSITIVE"
                );

        TestScenario duplicateAi =
                new TestScenario(
                        "Valid request",
                        "Verify valid request",
                        "POSITIVE"
                );

        List<TestScenario> merged =
                merger.merge(
                        List.of(deterministic),
                        List.of(duplicateAi)
                );

        assertEquals(
                1,
                merged.size()
        );
    }
}