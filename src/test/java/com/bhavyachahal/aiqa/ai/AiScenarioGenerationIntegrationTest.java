package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.parser.OpenApiSpecificationParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "RUN_OPENAI_INTEGRATION_TESTS",
        matches = "true"
)
class AiScenarioGenerationIntegrationTest {

    @Autowired
    private OpenApiSpecificationParser parser;

    @Autowired
    private HybridScenarioGenerator hybridScenarioGenerator;

    @Test
    void shouldGenerateSemanticAiScenarios()
            throws Exception {

        String specification =
                Files.readString(
                        Path.of(
                                "src/test/resources/ai-payment-api.yaml"
                        )
                );

        List<ApiEndpoint> endpoints =
                parser.parseEndpoints(
                        specification,
                        null
                );

        assertFalse(
                endpoints.isEmpty()
        );

        ApiEndpoint endpoint =
                endpoints.get(0);

        List<TestScenario> scenarios =
                hybridScenarioGenerator.generateScenarios(
                        endpoint
                );

        scenarios.forEach(
                scenario ->
                        System.out.println(
                                scenario.getType()
                                        + " | "
                                        + scenario.getName()
                                        + " | "
                                        + scenario.getDescription()
                        )
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(
                                scenario ->
                                        "AI_EXECUTABLE".equals(
                                                scenario.getType()
                                        )
                                                || "AI_RECOMMENDATION".equals(
                                                scenario.getType()
                                        )
                        )
        );
    }
}