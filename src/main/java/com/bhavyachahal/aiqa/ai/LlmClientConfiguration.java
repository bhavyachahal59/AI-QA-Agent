package com.bhavyachahal.aiqa.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmClientConfiguration {

    @Bean
    public LlmClient llmClient() {

        String apiKey =
                System.getenv(
                        "OPENAI_API_KEY"
                );

        if (apiKey == null
                || apiKey.isBlank()) {

            return new NoOpLlmClient();
        }

        OpenAIClient client =
                OpenAIOkHttpClient.builder()
                        .apiKey(apiKey)
                        .build();

        return new OpenAiLlmClient(
                client,
                "gpt-5.6-luna"
        );
    }
}