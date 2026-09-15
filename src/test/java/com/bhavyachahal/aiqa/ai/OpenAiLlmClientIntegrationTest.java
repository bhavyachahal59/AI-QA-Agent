package com.bhavyachahal.aiqa.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnabledIfEnvironmentVariable(
        named = "RUN_OPENAI_INTEGRATION_TESTS",
        matches = "true"
)
class OpenAiLlmClientIntegrationTest {

    @Test
    void shouldCallOpenAiSuccessfully() {

        String apiKey =
                System.getenv(
                        "OPENAI_API_KEY"
                );

        OpenAIClient client =
                OpenAIOkHttpClient.builder()
                        .apiKey(apiKey)
                        .build();

        OpenAiLlmClient llmClient =
                new OpenAiLlmClient(
                        client,
                        "gpt-5.6-luna"
                );

        String response =
                llmClient.generate(
                        """
                        Return only this JSON array:
                        [
                          {
                            "name": "AI connection successful",
                            "description": "OpenAI API connection works",
                            "type": "AI_SEMANTIC"
                          }
                        ]
                        """
                );

        System.out.println(
                "OPENAI RESPONSE:"
        );

        System.out.println(
                response
        );

        assertNotNull(
                response
        );

        assertFalse(
                response.isBlank()
        );
    }
}