package com.bhavyachahal.aiqa.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LlmClientConfigurationTest {

    @Test
    void shouldUseNoOpClientWhenApiKeyIsMissing() {

        String originalApiKey =
                System.getenv(
                        "OPENAI_API_KEY"
                );

        if (originalApiKey != null
                && !originalApiKey.isBlank()) {

            return;
        }

        LlmClientConfiguration configuration =
                new LlmClientConfiguration();

        LlmClient client =
                configuration.llmClient();

        assertInstanceOf(
                NoOpLlmClient.class,
                client
        );
    }
}