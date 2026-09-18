package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultAiScenarioGeneratorTest {

    @Test
    void shouldGenerateExecutableAiScenarioAndResolveExpectedStatus() {

        AiScenarioPromptBuilder promptBuilder =
                mock(AiScenarioPromptBuilder.class);

        LlmClient llmClient =
                mock(LlmClient.class);

        AiExpectedStatusResolver expectedStatusResolver =
                mock(AiExpectedStatusResolver.class);

        AiScenarioResponseParser responseParser =
                new AiScenarioResponseParser();

        ApiEndpoint endpoint =
                new ApiEndpoint();

        when(
                promptBuilder.build(endpoint)
        ).thenReturn(
                "prompt"
        );

        when(
                llmClient.generate("prompt")
        ).thenReturn(
                """
                [
                  {
                    "name": "Reject duplicate transaction",
                    "description": "Verify duplicate transaction is rejected",
                    "type": "AI_EXECUTABLE",
                    "expectedOutcome": "REJECT",
                    "requestBody": {
                      "transactionId": "txn-1"
                    }
                  }
                ]
                """
        );

        when(
                expectedStatusResolver.resolve(
                        endpoint,
                        "REJECT"
                )
        ).thenReturn(
                "400"
        );

        DefaultAiScenarioGenerator generator =
                new DefaultAiScenarioGenerator(
                        promptBuilder,
                        llmClient,
                        responseParser,
                        expectedStatusResolver
                );

        List<TestScenario> scenarios =
                generator.generateScenarios(
                        endpoint
                );

        assertEquals(
                1,
                scenarios.size()
        );

        TestScenario scenario =
                scenarios.get(0);

        assertEquals(
                "Reject duplicate transaction",
                scenario.getName()
        );

        assertEquals(
                "AI_EXECUTABLE",
                scenario.getType()
        );

        assertEquals(
                "REJECT",
                scenario.getExpectedOutcome()
        );

        assertEquals(
                "txn-1",
                scenario.getRequestPayload()
                        .getFields()
                        .get("transactionId")
        );

        assertEquals(
                "400",
                scenario.getExpectedStatusCode()
        );

        verify(
                expectedStatusResolver
        ).resolve(
                endpoint,
                "REJECT"
        );
    }

    @Test
    void shouldLeaveExpectedStatusUnverifiedWhenStatusIsAmbiguous() {

        AiScenarioPromptBuilder promptBuilder =
                mock(AiScenarioPromptBuilder.class);

        LlmClient llmClient =
                mock(LlmClient.class);

        AiExpectedStatusResolver expectedStatusResolver =
                mock(AiExpectedStatusResolver.class);

        AiScenarioResponseParser responseParser =
                new AiScenarioResponseParser();

        ApiEndpoint endpoint =
                new ApiEndpoint();

        when(
                promptBuilder.build(endpoint)
        ).thenReturn(
                "prompt"
        );

        when(
                llmClient.generate("prompt")
        ).thenReturn(
                """
                [
                  {
                    "name": "Reject transfer to same account",
                    "description": "Verify self-transfer is rejected",
                    "type": "AI_EXECUTABLE",
                    "expectedOutcome": "REJECT",
                    "requestBody": {
                      "sourceAccountId": "account-1",
                      "destinationAccountId": "account-1",
                      "amount": 100
                    }
                  }
                ]
                """
        );

        when(
                expectedStatusResolver.resolve(
                        endpoint,
                        "REJECT"
                )
        ).thenReturn(
                null
        );

        DefaultAiScenarioGenerator generator =
                new DefaultAiScenarioGenerator(
                        promptBuilder,
                        llmClient,
                        responseParser,
                        expectedStatusResolver
                );

        List<TestScenario> scenarios =
                generator.generateScenarios(
                        endpoint
                );

        assertEquals(
                1,
                scenarios.size()
        );

        TestScenario scenario =
                scenarios.get(0);

        assertEquals(
                "AI_EXECUTABLE",
                scenario.getType()
        );

        assertEquals(
                "REJECT",
                scenario.getExpectedOutcome()
        );

        assertNull(
                scenario.getExpectedStatusCode()
        );

        verify(
                expectedStatusResolver
        ).resolve(
                endpoint,
                "REJECT"
        );
    }
}