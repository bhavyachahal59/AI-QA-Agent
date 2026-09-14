package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAiScenarioGenerator
        implements AiScenarioGenerator {

    private final AiScenarioPromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final AiScenarioResponseParser responseParser;

    public DefaultAiScenarioGenerator(
            AiScenarioPromptBuilder promptBuilder,
            LlmClient llmClient,
            AiScenarioResponseParser responseParser) {

        this.promptBuilder =
                promptBuilder;

        this.llmClient =
                llmClient;

        this.responseParser =
                responseParser;
    }

    @Override
    public List<TestScenario> generateScenarios(
            ApiEndpoint endpoint) {

        String prompt =
                promptBuilder.build(
                        endpoint
                );

        String response =
                llmClient.generate(
                        prompt
                );

        return responseParser.parse(
                response
        );
    }
}