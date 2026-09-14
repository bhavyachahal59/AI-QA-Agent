package com.bhavyachahal.aiqa.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoOpLlmClientTest {

    @Test
    void shouldReturnEmptyJsonArray() {

        LlmClient client =
                new NoOpLlmClient();

        String response =
                client.generate(
                        "test prompt"
                );

        assertEquals(
                "[]",
                response
        );
    }
}