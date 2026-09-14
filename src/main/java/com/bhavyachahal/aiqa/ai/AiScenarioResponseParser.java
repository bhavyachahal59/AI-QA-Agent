package com.bhavyachahal.aiqa.ai;

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

            List<Map<String, String>> rawScenarios =
                    objectMapper.readValue(
                            response,
                            new TypeReference<>() {
                            }
                    );

            List<TestScenario> scenarios =
                    new ArrayList<>();

            for (Map<String, String> rawScenario :
                    rawScenarios) {

                String name =
                        rawScenario.get("name");

                String description =
                        rawScenario.get("description");

                String type =
                        rawScenario.get("type");

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

                scenarios.add(
                        scenario
                );
            }

            return scenarios;

        } catch (Exception exception) {

            return List.of();
        }
    }
}