package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultAiScenarioGeneratorTest {

    @Test
    void shouldGenerateAiScenariosFromLlmResponse() {

        AiScenarioPromptBuilder promptBuilder =
                mock(AiScenarioPromptBuilder.class);

        LlmClient llmClient =
                mock(LlmClient.class);

        AiScenarioResponseParser responseParser =
                new AiScenarioResponseParser(
                        new ObjectMapper()
                );

        DefaultAiScenarioGenerator generator =
                new DefaultAiScenarioGenerator(
                        promptBuilder,
                        llmClient,
                        responseParser
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        String prompt =
                "generated prompt";

        String response =
                """
                [
                  {
                    "name": "Duplicate transaction",
                    "description": "Verify duplicate transaction handling",
                    "type": "AI_SEMANTIC"
                  }
                ]
                """;

        when(
                promptBuilder.build(
                        endpoint
                )
        ).thenReturn(
                prompt
        );

        when(
                llmClient.generate(
                        prompt
                )
        ).thenReturn(
                response
        );

        List<TestScenario> scenarios =
                generator.generateScenarios(
                        endpoint
                );

        assertEquals(
                1,
                scenarios.size()
        );

        assertEquals(
                "Duplicate transaction",
                scenarios.get(0)
                        .getName()
        );

        assertEquals(
                "AI_SEMANTIC",
                scenarios.get(0)
                        .getType()
        );
    }
}