package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AiScenarioResponseParser {

    private final ObjectMapper objectMapper;

    public AiScenarioResponseParser() {
        this.objectMapper =
                new ObjectMapper();
    }

    public AiScenarioResponseParser(
            ObjectMapper objectMapper) {

        this.objectMapper =
                objectMapper;
    }

    public List<TestScenario> parse(
            String response) {

        if (response == null
                || response.isBlank()) {

            return List.of();
        }

        try {

            List<Map<String, Object>> rawScenarios =
                    objectMapper.readValue(
                            response,
                            new TypeReference<>() {
                            }
                    );

            List<TestScenario> scenarios =
                    new ArrayList<>();

            for (Map<String, Object> rawScenario :
                    rawScenarios) {

                String name =
                        asString(
                                rawScenario.get("name")
                        );

                String description =
                        asString(
                                rawScenario.get("description")
                        );

                String type =
                        asString(
                                rawScenario.get("type")
                        );

                if (name == null
                        || name.isBlank()) {

                    continue;
                }

                TestScenario scenario =
                        new TestScenario(
                                name,
                                description,
                                type
                        );
                String expectedOutcome =
                        asString(
                                rawScenario.get(
                                        "expectedOutcome"
                                )
                        );

                scenario.setExpectedOutcome(
                        expectedOutcome
                );

                Object requestBody =
                        rawScenario.get(
                                "requestBody"
                        );

                if (requestBody
                        instanceof Map<?, ?> rawRequestBody) {

                    RequestPayload requestPayload =
                            new RequestPayload();

                    for (Map.Entry<?, ?> entry :
                            rawRequestBody.entrySet()) {

                        if (entry.getKey() == null) {
                            continue;
                        }

                        requestPayload.addField(
                                String.valueOf(
                                        entry.getKey()
                                ),
                                entry.getValue()
                        );
                    }

                    scenario.setRequestPayload(
                            requestPayload
                    );
                }

                scenarios.add(
                        scenario
                );
            }

            return scenarios;

        } catch (Exception exception) {

            return List.of();
        }
    }

    private String asString(
            Object value) {

        return value == null
                ? null
                : String.valueOf(value);
    }
}