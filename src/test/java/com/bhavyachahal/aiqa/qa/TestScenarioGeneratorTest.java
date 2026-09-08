package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestScenarioGeneratorTest {

    @Test
    void shouldGenerateParameterSpecificScenarios() {

        ApiEndpoint endpoint = new ApiEndpoint();

        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");

        ApiParameter idParameter =
                new ApiParameter(
                        "id",
                        "path",
                        true,
                        "integer"
                );

        endpoint.setParameters(
                List.of(idParameter)
        );

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        assertEquals(4, scenarios.size());

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Boundary value: id"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Missing required parameter: id"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Invalid parameter: id"))
        );
    }
}