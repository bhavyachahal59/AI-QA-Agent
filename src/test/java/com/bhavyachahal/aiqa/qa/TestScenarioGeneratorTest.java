package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;

class TestScenarioGeneratorTest {

    @Test
    void shouldGenerateRequestBodyFieldScenarios() {

        ApiEndpoint endpoint = new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("POST");

        ApiRequestBody requestBody =
                new ApiRequestBody(
                        "application/json",
                        "object",
                        null
                );

        requestBody.setFields(
                List.of(
                        new ApiRequestBodyField(
                                "name",
                                "string",
                                true,
                                null
                        ),
                        new ApiRequestBodyField(
                                "email",
                                "string",
                                true,
                                "email"
                        ),
                        new ApiRequestBodyField(
                                "age",
                                "integer",
                                false,
                                "int32"
                        )
                )
        );

        endpoint.setRequestBody(requestBody);

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Missing required field: name"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Missing required field: email"))
        );

        assertTrue(
                scenarios.stream()
                        .noneMatch(scenario ->
                                scenario.getName()
                                        .equals("Missing required field: age"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Invalid email: email"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Invalid integer: age"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Empty string: name"))
        );
    }
}