package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAiScenarioGenerator
        implements AiScenarioGenerator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    DefaultAiScenarioGenerator.class
            );

    private final AiScenarioPromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final AiScenarioResponseParser responseParser;
    private final AiScenarioSchemaFilter schemaFilter;
    private final AiExpectedStatusResolver expectedStatusResolver;

    public DefaultAiScenarioGenerator(
            AiScenarioPromptBuilder promptBuilder,
            LlmClient llmClient,
            AiScenarioResponseParser responseParser,
            AiScenarioSchemaFilter schemaFilter,
            AiExpectedStatusResolver expectedStatusResolver) {

        this.promptBuilder = promptBuilder;
        this.llmClient = llmClient;
        this.responseParser = responseParser;
        this.schemaFilter = schemaFilter;
        this.expectedStatusResolver =
                expectedStatusResolver;
    }

    @Override
    public List<TestScenario> generateScenarios(
            ApiEndpoint endpoint) {

        try {

            String prompt =
                    promptBuilder.build(endpoint);

            String response =
                    llmClient.generate(prompt);

            List<TestScenario> parsedScenarios =
                    responseParser.parse(response);

            List<TestScenario> scenarios =
                    schemaFilter.filter(
                            endpoint,
                            parsedScenarios
                    );

            for (TestScenario scenario : scenarios) {

                if (!"AI_EXECUTABLE".equals(
                        scenario.getType())) {

                    continue;
                }

                String expectedStatusCode =
                        expectedStatusResolver.resolve(
                                endpoint,
                                scenario.getExpectedOutcome()
                        );

                scenario.setExpectedStatusCode(
                        expectedStatusCode
                );
            }

            return scenarios;

        } catch (Exception exception) {

            LOGGER.warn(
                    "AI scenario generation failed for {} {}. "
                            + "Continuing without AI-generated scenarios.",
                    endpoint.getMethod(),
                    endpoint.getPath(),
                    exception
            );

            return List.of();
        }
    }
}