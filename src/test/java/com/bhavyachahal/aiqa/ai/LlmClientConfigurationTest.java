package com.bhavyachahal.aiqa.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LlmClientConfigurationTest {

    @Test
    void shouldUseNoOpClientWhenAiIsDisabled() {

        LlmClientConfiguration configuration =
                new LlmClientConfiguration();

        LlmClient client =
                configuration.llmClient(
                        false
                );

        assertInstanceOf(
                NoOpLlmClient.class,
                client
        );
    }

    @Test
    void shouldUseNoOpClientWhenAiIsEnabledButApiKeyIsMissing() {

        String apiKey =
                System.getenv(
                        "OPENAI_API_KEY"
                );

        if (apiKey != null
                && !apiKey.isBlank()) {
            return;
        }

        LlmClientConfiguration configuration =
                new LlmClientConfiguration();

        LlmClient client =
                configuration.llmClient(
                        true
                );

        assertInstanceOf(
                NoOpLlmClient.class,
                client
        );
    }
}