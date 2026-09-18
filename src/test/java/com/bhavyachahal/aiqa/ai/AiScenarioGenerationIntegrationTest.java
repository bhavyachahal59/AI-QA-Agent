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

import static org.junit.jupiter.api.Assertions.*;

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

        List<TestScenario> executableAiScenarios =
                scenarios.stream()
                        .filter(
                                scenario ->
                                        "AI_EXECUTABLE".equals(
                                                scenario.getType()
                                        )
                        )
                        .toList();

        for (TestScenario scenario :
                executableAiScenarios) {

            assertTrue(
                    scenario.getRequestPayload() != null,
                    "AI_EXECUTABLE scenario must contain request payload: "
                            + scenario.getName()
            );

            assertFalse(
                    scenario.getRequestPayload()
                            .getFields()
                            .isEmpty(),
                    "AI_EXECUTABLE scenario must contain request fields: "
                            + scenario.getName()
            );

            System.out.println(
                    "AI REQUEST PAYLOAD | "
                            + scenario.getName()
                            + " | "
                            + scenario.getRequestPayload()
                            .getFields()
            );

            System.out.println(
                    "AI EXPECTATION | "
                            + scenario.getName()
                            + " | outcome="
                            + scenario.getExpectedOutcome()
                            + " | status="
                            + scenario.getExpectedStatusCode()
            );
        }

        for (TestScenario scenario :
                executableAiScenarios) {

            if ("REJECT".equals(
                    scenario.getExpectedOutcome()
            )) {

                assertNull(
                        scenario.getExpectedStatusCode(),
                        "Ambiguous REJECT outcome must remain unverified "
                                + "when multiple 4xx responses are documented"
                );
            }
        }
    }
}