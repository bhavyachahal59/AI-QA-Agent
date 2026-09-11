package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        TestScenario missingEmailScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Missing required field: email"))
                        .findFirst()
                        .orElseThrow();

        assertFalse(
                missingEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .containsKey("email")
        );

        assertEquals(
                "sample-name",
                missingEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
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
                Integer.MAX_VALUE,
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

    @Test
    void shouldGenerateStructuredParameterValues() {

        ApiEndpoint endpoint = new ApiEndpoint();

        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");

        endpoint.setParameters(
                List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                                "id",
                                "path",
                                true,
                                "integer"
                        )
                )
        );

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        TestScenario validScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Valid request"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                1,
                validScenario
                        .getParameterValues()
                        .get("id")
        );

        TestScenario invalidScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Invalid parameter: id"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "not-an-integer",
                invalidScenario
                        .getParameterValues()
                        .get("id")
        );

        TestScenario boundaryScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Boundary value: id"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                Integer.MAX_VALUE,
                boundaryScenario
                        .getParameterValues()
                        .get("id")
        );
    }

    @Test
    void shouldKeepGeneratedRequestPayloadsIndependent() {

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
                        )
                )
        );

        endpoint.setRequestBody(requestBody);

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        TestScenario validScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Valid request"))
                        .findFirst()
                        .orElseThrow();

        TestScenario invalidEmailScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Invalid email: email"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "sample-email",
                validScenario
                        .getRequestPayload()
                        .getFields()
                        .get("email")
        );

        assertEquals(
                "invalid-email",
                invalidEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("email")
        );

        assertEquals(
                "sample-name",
                validScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
        );

        assertEquals(
                "sample-name",
                invalidEmailScenario
                        .getRequestPayload()
                        .getFields()
                        .get("name")
        );
    }

    @Test
    void shouldAssignSuccessStatusCodeToPositiveScenarios() {

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        endpoint.setResponses(
                List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "400",
                                "Bad request",
                                "application/json",
                                null,
                                null
                        ),
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "201",
                                "Created",
                                "application/json",
                                null,
                                null
                        )
                )
        );

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        TestScenario validRequestScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Valid request"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "201",
                validRequestScenario.getExpectedStatusCode()
        );

        TestScenario expectedResponseScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Expected response"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "201",
                expectedResponseScenario.getExpectedStatusCode()
        );
    }

    @Test
    void shouldAssignClientErrorStatusCodeToValidationScenarios() {

        ApiEndpoint endpoint =
                new ApiEndpoint();

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
                                "email",
                                "string",
                                true,
                                "email"
                        )
                )
        );

        endpoint.setRequestBody(requestBody);

        endpoint.setResponses(
                List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "201",
                                "Created",
                                "application/json",
                                null,
                                null
                        ),
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "400",
                                "Bad request",
                                "application/json",
                                null,
                                null
                        )
                )
        );

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        List<TestScenario> scenarios =
                generator.generate(endpoint);

        TestScenario missingEmailScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Missing required field: email"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "400",
                missingEmailScenario.getExpectedStatusCode()
        );

        TestScenario invalidEmailScenario =
                scenarios.stream()
                        .filter(scenario ->
                                scenario.getName()
                                        .equals("Invalid email: email"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "400",
                invalidEmailScenario.getExpectedStatusCode()
        );
    }
}