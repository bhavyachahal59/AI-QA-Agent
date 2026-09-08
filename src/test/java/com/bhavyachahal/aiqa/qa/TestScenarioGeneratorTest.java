package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        TestScenario invalidEmailScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Invalid email: email"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "invalid-email",
                invalidEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("email")
        );

        assertEquals(
                "sample-name",
                invalidEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
        );

        assertEquals(
                1,
                invalidEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("age")
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
                                        .equals("Negative integer: age"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Zero value: age"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Large integer: age"))
        );

        assertTrue(
                scenarios.stream()
                        .anyMatch(scenario ->
                                scenario.getName()
                                        .equals("Empty string: name"))
        );

        TestScenario invalidIntegerScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Invalid integer: age"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "not-an-integer",
                invalidIntegerScenario
                        .getRequestPayload()
                        .getFields()
                        .get("age")
        );

        assertEquals(
                "sample-name",
                invalidIntegerScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
        );

        assertEquals(
                "sample-email",
                invalidIntegerScenario
                        .getRequestPayload()
                        .getFields()
                        .get("email")
        );

        TestScenario negativeIntegerScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Negative integer: age"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                -1,
                negativeIntegerScenario
                        .getRequestPayload()
                        .getFields()
                        .get("age")
        );

        TestScenario zeroValueScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Zero value: age"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                0,
                zeroValueScenario
                        .getRequestPayload()
                        .getFields()
                        .get("age")
        );

        TestScenario largeIntegerScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Large integer: age"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                2147483647,
                largeIntegerScenario
                        .getRequestPayload()
                        .getFields()
                        .get("age")
        );

        TestScenario emptyStringScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Empty string: name"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "",
                emptyStringScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
        );

        TestScenario validRequestScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Valid request"))
                        .findFirst()
                        .orElseThrow();

        RequestPayload requestPayload =
                validRequestScenario.getRequestPayload();

        assertEquals(
                "sample-name",
                requestPayload.getFields().get("name")
        );

        assertEquals(
                "sample-email",
                requestPayload.getFields().get("email")
        );

        assertEquals(
                1,
                requestPayload.getFields().get("age")
        );
    }
}