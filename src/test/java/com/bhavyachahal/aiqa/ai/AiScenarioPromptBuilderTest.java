package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AiScenarioPromptBuilderTest {

    @Test
    void shouldIncludeEndpointInformationInPrompt() {

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setMethod("GET");
        endpoint.setPath("/users/{id}");
        endpoint.setSummary("Get a user");

        ApiParameter parameter =
                new ApiParameter(
                        "id",
                        "path",
                        true,
                        "integer"
                );

        endpoint.setParameters(
                List.of(parameter)
        );

        AiScenarioPromptBuilder builder =
                new AiScenarioPromptBuilder();

        String prompt =
                builder.build(endpoint);

        assertTrue(
                prompt.contains("GET")
        );

        assertTrue(
                prompt.contains("/users/{id}")
        );

        assertTrue(
                prompt.contains("Get a user")
        );

        assertTrue(
                prompt.contains("id")
        );

        assertTrue(
                prompt.contains("AI_EXECUTABLE")
        );

        assertTrue(
                prompt.contains("AI_RECOMMENDATION")
        );

        assertTrue(
                prompt.contains(
                        "Do not generate any scenario that can already be derived from"
                )
        );

        assertTrue(
                prompt.contains(
                        "values below minimum or above maximum"
                )
        );

        assertTrue(
                prompt.contains(
                        "Prefer cross-field relationships and business rules"
                )
        );

        assertTrue(
                prompt.contains(
                        "\"expectedOutcome\": \"REJECT\""
                )
        );

        assertTrue(
                prompt.contains(
                        "Do NOT generate or guess HTTP status codes"
                )
        );
    }
}