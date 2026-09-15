package com.bhavyachahal.aiqa.ai;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAiLlmClient
        implements LlmClient {

    private final OpenAIClient client;
    private final String model;

    public OpenAiLlmClient(
            OpenAIClient client,
            String model) {

        this.client = client;
        this.model = model;
    }

    @Override
    public String generate(
            String prompt) {

        ResponseCreateParams params =
                ResponseCreateParams.builder()
                        .input(prompt)
                        .model(model)
                        .build();

        Response response =
                client.responses()
                        .create(params);

        return response.output()
                .stream()
                .flatMap(item ->
                        item.message()
                                .stream()
                )
                .flatMap(message ->
                        message.content()
                                .stream()
                )
                .flatMap(content ->
                        content.outputText()
                                .stream()
                )
                .map(outputText ->
                        outputText.text()
                )
                .findFirst()
                .orElse("[]");
    }
}