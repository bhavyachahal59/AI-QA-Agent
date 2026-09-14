package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiScenarioResponseParserTest {

    private final AiScenarioResponseParser parser =
            new AiScenarioResponseParser(
                    new ObjectMapper()
            );

    @Test
    void shouldParseAiScenarios() {

        String response =
                """
                [
                  {
                    "name": "Insufficient balance",
                    "description": "Verify transaction rejection when balance is insufficient",
                    "type": "AI_SEMANTIC"
                  },
                  {
                    "name": "Duplicate transaction",
                    "description": "Verify duplicate transaction handling",
                    "type": "AI_SEMANTIC"
                  }
                ]
                """;

        List<TestScenario> scenarios =
                parser.parse(response);

        assertEquals(
                2,
                scenarios.size()
        );

        assertEquals(
                "Insufficient balance",
                scenarios.get(0)
                        .getName()
        );

        assertEquals(
                "AI_SEMANTIC",
                scenarios.get(0)
                        .getType()
        );
    }

    @Test
    void shouldReturnEmptyListForInvalidJson() {

        List<TestScenario> scenarios =
                parser.parse(
                        "not-json"
                );

        assertTrue(
                scenarios.isEmpty()
        );
    }

    @Test
    void shouldIgnoreScenarioWithoutName() {

        String response =
                """
                [
                  {
                    "description": "Missing name",
                    "type": "AI_SEMANTIC"
                  }
                ]
                """;

        List<TestScenario> scenarios =
                parser.parse(response);

        assertTrue(
                scenarios.isEmpty()
        );
    }
}