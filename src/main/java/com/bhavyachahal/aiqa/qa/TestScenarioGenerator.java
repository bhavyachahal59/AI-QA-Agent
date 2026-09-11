package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestScenarioGenerator {

    public List<TestScenario> generate(ApiEndpoint endpoint) {

        List<TestScenario> scenarios = new ArrayList<>();

        generateParameterScenarios(
                endpoint,
                scenarios
        );

        TestScenario validRequestScenario =
                new TestScenario(
                        "Valid request",
                        "Verify the endpoint accepts a valid request",
                        "POSITIVE"
                );

        if (hasRequestBody(endpoint)) {

            validRequestScenario.setRequestPayload(
                    generateBaselinePayload(
                            endpoint.getRequestBody()
                    )
            );
        }

        addBaselineParameterValues(
                endpoint,
                validRequestScenario
        );

        validRequestScenario.setExpectedStatusCode(
                findSuccessStatusCode(endpoint)
        );

        scenarios.add(validRequestScenario);

        generateMissingRequiredFieldScenarios(
                endpoint,
                scenarios
        );

        generateRequestBodyFieldScenarios(
                endpoint,
                scenarios
        );

        if (endpoint.getResponses() != null
                && !endpoint.getResponses().isEmpty()) {

            TestScenario expectedResponseScenario =
                    new TestScenario(
                            "Expected response",
                            "Verify the endpoint returns an expected response",
                            "POSITIVE"
                    );

            expectedResponseScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    expectedResponseScenario
            );
        }

        return scenarios;
    }

    private void generateParameterScenarios(
            ApiEndpoint endpoint,
            List<TestScenario> scenarios) {

        if (endpoint.getParameters() == null) {
            return;
        }

        endpoint.getParameters()
                .stream()
                .filter(ApiParameter::isRequired)
                .forEach(parameter -> {

                    TestScenario missingScenario =
                            new TestScenario(
                                    "Missing required parameter: "
                                            + parameter.getName(),
                                    "Verify the endpoint rejects a request when the required "
                                            + parameter.getLocation()
                                            + " parameter '"
                                            + parameter.getName()
                                            + "' is missing",
                                    "VALIDATION"
                            );

                    missingScenario.setExpectedStatusCode(
                            findClientErrorStatusCode(endpoint)
                    );

                    scenarios.add(missingScenario);

                    TestScenario invalidScenario =
                            new TestScenario(
                                    "Invalid parameter: "
                                            + parameter.getName(),
                                    "Verify the endpoint handles an invalid value for the "
                                            + parameter.getLocation()
                                            + " parameter '"
                                            + parameter.getName()
                                            + "'",
                                    "NEGATIVE"
                            );

                    invalidScenario.addParameterValue(
                            parameter.getName(),
                            generateInvalidParameterValue(parameter)
                    );

                    invalidScenario.setExpectedStatusCode(
                            findClientErrorStatusCode(endpoint)
                    );

                    scenarios.add(invalidScenario);

                    if ("integer".equalsIgnoreCase(
                            parameter.getType())) {

                        generateIntegerParameterBoundaryScenarios(
                                endpoint,
                                parameter,
                                scenarios
                        );
                    }

                    if ("string".equalsIgnoreCase(
                            parameter.getType())) {

                        if (parameter.getMinLength() != null
                                || parameter.getMaxLength() != null) {

                            generateStringParameterLengthScenarios(
                                    endpoint,
                                    parameter,
                                    scenarios
                            );

                        } else {

                            TestScenario emptyScenario =
                                    new TestScenario(
                                            "Empty value: "
                                                    + parameter.getName(),
                                            "Verify the endpoint handles an empty string for the "
                                                    + parameter.getLocation()
                                                    + " parameter '"
                                                    + parameter.getName()
                                                    + "'",
                                            "BOUNDARY"
                                    );

                            emptyScenario.addParameterValue(
                                    parameter.getName(),
                                    ""
                            );

                            scenarios.add(emptyScenario);
                        }
                        if (parameter.getPattern() != null
                                && !parameter.getPattern().isBlank()) {

                            generateParameterPatternScenario(
                                    endpoint,
                                    parameter,
                                    scenarios
                            );
                        }
                    }
                });
    }

    private void generateParameterPatternScenario(
            ApiEndpoint endpoint,
            ApiParameter parameter,
            List<TestScenario> scenarios) {

        String invalidValue =
                generatePatternViolatingValue(
                        parameter.getPattern()
                );

        TestScenario scenario =
                new TestScenario(
                        "Invalid pattern: "
                                + parameter.getName(),
                        "Verify the endpoint rejects a value that does not match the required pattern "
                                + "for parameter '"
                                + parameter.getName()
                                + "'",
                        "VALIDATION"
                );

        scenario.addParameterValue(
                parameter.getName(),
                invalidValue
        );

        scenario.setExpectedStatusCode(
                findClientErrorStatusCode(endpoint)
        );

        scenarios.add(scenario);
    }

    private void generateStringParameterLengthScenarios(
            ApiEndpoint endpoint,
            ApiParameter parameter,
            List<TestScenario> scenarios) {

        if (parameter.getMinLength() != null) {

            int minLength =
                    parameter.getMinLength();

            if (minLength > 0) {

                TestScenario belowMinimumScenario =
                        new TestScenario(
                                "Below minimum length: "
                                        + parameter.getName(),
                                "Verify the endpoint rejects a string shorter than the minimum length "
                                        + "for parameter '"
                                        + parameter.getName()
                                        + "'",
                                "VALIDATION"
                        );

                belowMinimumScenario.addParameterValue(
                        parameter.getName(),
                        "a".repeat(
                                minLength - 1
                        )
                );

                belowMinimumScenario.setExpectedStatusCode(
                        findClientErrorStatusCode(endpoint)
                );

                scenarios.add(
                        belowMinimumScenario
                );
            }

            TestScenario minimumBoundaryScenario =
                    new TestScenario(
                            "Minimum length: "
                                    + parameter.getName(),
                            "Verify the endpoint accepts a string at the minimum allowed length "
                                    + "for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "BOUNDARY"
                    );

            minimumBoundaryScenario.addParameterValue(
                    parameter.getName(),
                    "a".repeat(
                            minLength
                    )
            );

            minimumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    minimumBoundaryScenario
            );
        }

        if (parameter.getMaxLength() != null) {

            int maxLength =
                    parameter.getMaxLength();

            TestScenario maximumBoundaryScenario =
                    new TestScenario(
                            "Maximum length: "
                                    + parameter.getName(),
                            "Verify the endpoint accepts a string at the maximum allowed length "
                                    + "for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "BOUNDARY"
                    );

            maximumBoundaryScenario.addParameterValue(
                    parameter.getName(),
                    "a".repeat(
                            maxLength
                    )
            );

            maximumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    maximumBoundaryScenario
            );

            TestScenario aboveMaximumScenario =
                    new TestScenario(
                            "Above maximum length: "
                                    + parameter.getName(),
                            "Verify the endpoint rejects a string longer than the maximum length "
                                    + "for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "VALIDATION"
                    );

            aboveMaximumScenario.addParameterValue(
                    parameter.getName(),
                    "a".repeat(
                            maxLength + 1
                    )
            );

            aboveMaximumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(
                    aboveMaximumScenario
            );
        }
    }

    private void generateIntegerParameterBoundaryScenarios(
            ApiEndpoint endpoint,
            ApiParameter parameter,
            List<TestScenario> scenarios) {

        if (parameter.getMinimum() == null
                && parameter.getMaximum() == null) {

            TestScenario boundaryScenario =
                    new TestScenario(
                            "Boundary value: "
                                    + parameter.getName(),
                            "Verify the endpoint handles boundary integer values for the "
                                    + parameter.getLocation()
                                    + " parameter '"
                                    + parameter.getName()
                                    + "'",
                            "BOUNDARY"
                    );

            boundaryScenario.addParameterValue(
                    parameter.getName(),
                    Integer.MAX_VALUE
            );

            scenarios.add(boundaryScenario);

            return;
        }

        if (parameter.getMinimum() != null) {

            int minimum =
                    parameter.getMinimum()
                            .intValue();

            TestScenario belowMinimumScenario =
                    new TestScenario(
                            "Below minimum: "
                                    + parameter.getName(),
                            "Verify the endpoint rejects a value below the minimum for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "VALIDATION"
                    );

            belowMinimumScenario.addParameterValue(
                    parameter.getName(),
                    minimum - 1
            );

            belowMinimumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(belowMinimumScenario);

            TestScenario minimumBoundaryScenario =
                    new TestScenario(
                            "Minimum boundary: "
                                    + parameter.getName(),
                            "Verify the endpoint accepts the minimum allowed value for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "BOUNDARY"
                    );

            minimumBoundaryScenario.addParameterValue(
                    parameter.getName(),
                    minimum
            );

            minimumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(minimumBoundaryScenario);
        }

        if (parameter.getMaximum() != null) {

            int maximum =
                    parameter.getMaximum()
                            .intValue();

            TestScenario maximumBoundaryScenario =
                    new TestScenario(
                            "Maximum boundary: "
                                    + parameter.getName(),
                            "Verify the endpoint accepts the maximum allowed value for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "BOUNDARY"
                    );

            maximumBoundaryScenario.addParameterValue(
                    parameter.getName(),
                    maximum
            );

            maximumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(maximumBoundaryScenario);

            TestScenario aboveMaximumScenario =
                    new TestScenario(
                            "Above maximum: "
                                    + parameter.getName(),
                            "Verify the endpoint rejects a value above the maximum for parameter '"
                                    + parameter.getName()
                                    + "'",
                            "VALIDATION"
                    );

            aboveMaximumScenario.addParameterValue(
                    parameter.getName(),
                    maximum + 1
            );

            aboveMaximumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(aboveMaximumScenario);
        }
    }

    private void addBaselineParameterValues(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        if (endpoint.getParameters() == null) {
            return;
        }

        endpoint.getParameters()
                .stream()
                .filter(ApiParameter::isRequired)
                .forEach(parameter ->
                        scenario.addParameterValue(
                                parameter.getName(),
                                generateBaselineParameterValue(
                                        parameter
                                )
                        )
                );
    }

    private Object generateBaselineParameterValue(
            ApiParameter parameter) {

        if ("integer".equalsIgnoreCase(
                parameter.getType())) {

            if (parameter.getMinimum() != null) {
                return parameter.getMinimum()
                        .intValue();
            }

            return 1;
        }

        if ("number".equalsIgnoreCase(
                parameter.getType())) {

            if (parameter.getMinimum() != null) {
                return parameter.getMinimum()
                        .doubleValue();
            }

            return 1.0;
        }

        if ("boolean".equalsIgnoreCase(
                parameter.getType())) {

            return true;
        }

        return generateBaselineParameterStringValue(
                parameter
        );
    }

    private String generateBaselineParameterStringValue(
            ApiParameter parameter) {

        if (parameter.getPattern() == null
                || parameter.getPattern().isBlank()) {

            return adjustStringLength(
                    "sample-" + parameter.getName(),
                    parameter.getMinLength(),
                    parameter.getMaxLength()
            );
        }

        List<String> candidates =
                List.of(
                        "sample_" + parameter.getName(),
                        "sample",
                        "test123",
                        "abc",
                        "A1"
                );

        for (String candidate : candidates) {

            String adjustedCandidate =
                    adjustStringLength(
                            candidate,
                            parameter.getMinLength(),
                            parameter.getMaxLength()
                    );

            if (adjustedCandidate.matches(
                    parameter.getPattern()
            )) {

                return adjustedCandidate;
            }
        }

        return adjustStringLength(
                "a",
                parameter.getMinLength(),
                parameter.getMaxLength()
        );
    }

    private Object generateInvalidParameterValue(
            ApiParameter parameter) {

        if ("integer".equalsIgnoreCase(
                parameter.getType())) {

            return "not-an-integer";
        }

        if ("number".equalsIgnoreCase(
                parameter.getType())) {

            return "not-a-number";
        }

        if ("boolean".equalsIgnoreCase(
                parameter.getType())) {

            return "not-a-boolean";
        }

        return "invalid";
    }

    private void generateMissingRequiredFieldScenarios(
            ApiEndpoint endpoint,
            List<TestScenario> scenarios) {

        if (!hasRequestBody(endpoint)) {
            return;
        }

        endpoint.getRequestBody()
                .getFields()
                .stream()
                .filter(ApiRequestBodyField::isRequired)
                .forEach(field -> {

                    TestScenario scenario =
                            new TestScenario(
                                    "Missing required field: "
                                            + field.getName(),
                                    "Verify the endpoint rejects the request when required "
                                            + "request body field '"
                                            + field.getName()
                                            + "' is missing",
                                    "VALIDATION"
                            );

                    RequestPayload payload =
                            generateBaselinePayload(
                                    endpoint.getRequestBody()
                            ).copy();

                    payload.getFields()
                            .remove(field.getName());

                    scenario.setRequestPayload(
                            payload
                    );

                    scenario.setExpectedStatusCode(
                            findClientErrorStatusCode(endpoint)
                    );

                    scenarios.add(scenario);
                });
    }

    private void generateRequestBodyFieldScenarios(
            ApiEndpoint endpoint,
            List<TestScenario> scenarios) {

        if (!hasRequestBody(endpoint)) {
            return;
        }

        endpoint.getRequestBody()
                .getFields()
                .forEach(field -> {

                    if ("email".equalsIgnoreCase(
                            field.getFormat())) {

                        TestScenario scenario =
                                new TestScenario(
                                        "Invalid email: "
                                                + field.getName(),
                                        "Verify the endpoint rejects an invalid email "
                                                + "value for request body field '"
                                                + field.getName()
                                                + "'",
                                        "VALIDATION"
                                );

                        RequestPayload payload =
                                generateBaselinePayload(
                                        endpoint.getRequestBody()
                                ).copy();

                        payload.addField(
                                field.getName(),
                                "invalid-email"
                        );

                        scenario.setRequestPayload(
                                payload
                        );

                        scenario.setExpectedStatusCode(
                                findClientErrorStatusCode(endpoint)
                        );

                        scenarios.add(scenario);
                    }

                    if ("integer".equalsIgnoreCase(
                            field.getType())) {

                        addIntegerScenarios(
                                endpoint,
                                field,
                                scenarios
                        );
                    }

                    if ("string".equalsIgnoreCase(
                            field.getType())
                            && !"email".equalsIgnoreCase(
                            field.getFormat())) {

                        if (field.getPattern() != null
                                && !field.getPattern().isBlank()) {

                            addPatternValidationScenario(
                                    endpoint,
                                    field,
                                    scenarios
                            );
                        }

                        if (field.getMinLength() != null
                                || field.getMaxLength() != null) {

                            addSchemaAwareStringLengthScenarios(
                                    endpoint,
                                    field,
                                    scenarios
                            );

                        } else {

                            TestScenario scenario =
                                    createStringPayloadScenario(
                                            endpoint,
                                            field,
                                            "Empty string: "
                                                    + field.getName(),
                                            "Verify the endpoint validates an empty string "
                                                    + "for request body field '"
                                                    + field.getName()
                                                    + "'",
                                            "VALIDATION",
                                            ""
                                    );

                            scenario.setExpectedStatusCode(
                                    findClientErrorStatusCode(endpoint)
                            );

                            scenarios.add(scenario);
                        }
                    }
                });
    }

    private void addPatternValidationScenario(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            List<TestScenario> scenarios) {

        String invalidValue =
                generatePatternViolatingValue(
                        field.getPattern()
                );

        TestScenario scenario =
                createStringPayloadScenario(
                        endpoint,
                        field,
                        "Invalid pattern: "
                                + field.getName(),
                        "Verify the endpoint rejects a value that does not match the required pattern "
                                + "for request body field '"
                                + field.getName()
                                + "'",
                        "VALIDATION",
                        invalidValue
                );

        scenario.setExpectedStatusCode(
                findClientErrorStatusCode(endpoint)
        );

        scenarios.add(scenario);
    }

    private String generatePatternViolatingValue(
            String pattern) {

        List<String> candidates =
                List.of(
                        "invalid value!",
                        "###",
                        " ",
                        "invalid@value",
                        "123-INVALID!"
                );

        for (String candidate : candidates) {

            if (!candidate.matches(pattern)) {
                return candidate;
            }
        }

        return "invalid value!";
    }

    private void addIntegerScenarios(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            List<TestScenario> scenarios) {

        TestScenario invalidScenario =
                new TestScenario(
                        "Invalid integer: "
                                + field.getName(),
                        "Verify the endpoint rejects a non-integer "
                                + "value for request body field '"
                                + field.getName()
                                + "'",
                        "VALIDATION"
                );

        RequestPayload invalidPayload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        invalidPayload.addField(
                field.getName(),
                "not-an-integer"
        );

        invalidScenario.setRequestPayload(
                invalidPayload
        );

        invalidScenario.setExpectedStatusCode(
                findClientErrorStatusCode(endpoint)
        );

        scenarios.add(invalidScenario);

        if (field.getMinimum() != null
                || field.getMaximum() != null) {

            addSchemaAwareIntegerScenarios(
                    endpoint,
                    field,
                    scenarios
            );

            return;
        }

        addGenericIntegerScenarios(
                endpoint,
                field,
                scenarios
        );
    }

    private void addSchemaAwareIntegerScenarios(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            List<TestScenario> scenarios) {

        if (field.getMinimum() != null) {

            int minimum =
                    field.getMinimum()
                            .intValue();

            TestScenario belowMinimumScenario =
                    createIntegerPayloadScenario(
                            endpoint,
                            field,
                            "Below minimum: "
                                    + field.getName(),
                            "Verify the endpoint rejects a value below the minimum for request body field '"
                                    + field.getName()
                                    + "'",
                            "VALIDATION",
                            minimum - 1
                    );

            belowMinimumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(
                    belowMinimumScenario
            );

            TestScenario minimumBoundaryScenario =
                    createIntegerPayloadScenario(
                            endpoint,
                            field,
                            "Minimum boundary: "
                                    + field.getName(),
                            "Verify the endpoint accepts the minimum allowed value for request body field '"
                                    + field.getName()
                                    + "'",
                            "BOUNDARY",
                            minimum
                    );

            minimumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    minimumBoundaryScenario
            );
        }

        if (field.getMaximum() != null) {

            int maximum =
                    field.getMaximum()
                            .intValue();

            TestScenario maximumBoundaryScenario =
                    createIntegerPayloadScenario(
                            endpoint,
                            field,
                            "Maximum boundary: "
                                    + field.getName(),
                            "Verify the endpoint accepts the maximum allowed value for request body field '"
                                    + field.getName()
                                    + "'",
                            "BOUNDARY",
                            maximum
                    );

            maximumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    maximumBoundaryScenario
            );

            TestScenario aboveMaximumScenario =
                    createIntegerPayloadScenario(
                            endpoint,
                            field,
                            "Above maximum: "
                                    + field.getName(),
                            "Verify the endpoint rejects a value above the maximum for request body field '"
                                    + field.getName()
                                    + "'",
                            "VALIDATION",
                            maximum + 1
                    );

            aboveMaximumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(
                    aboveMaximumScenario
            );
        }
    }

    private TestScenario createIntegerPayloadScenario(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            String name,
            String description,
            String type,
            int value) {

        TestScenario scenario =
                new TestScenario(
                        name,
                        description,
                        type
                );

        RequestPayload payload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        payload.addField(
                field.getName(),
                value
        );

        scenario.setRequestPayload(
                payload
        );

        return scenario;
    }

    private void addGenericIntegerScenarios(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            List<TestScenario> scenarios) {

        TestScenario negativeScenario =
                createIntegerPayloadScenario(
                        endpoint,
                        field,
                        "Negative integer: "
                                + field.getName(),
                        "Verify the endpoint handles a negative integer value for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY",
                        -1
                );

        scenarios.add(
                negativeScenario
        );

        TestScenario zeroScenario =
                createIntegerPayloadScenario(
                        endpoint,
                        field,
                        "Zero value: "
                                + field.getName(),
                        "Verify the endpoint handles a zero value for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY",
                        0
                );

        scenarios.add(
                zeroScenario
        );

        TestScenario largeScenario =
                createIntegerPayloadScenario(
                        endpoint,
                        field,
                        "Large integer: "
                                + field.getName(),
                        "Verify the endpoint handles a large integer value for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY",
                        Integer.MAX_VALUE
                );

        scenarios.add(
                largeScenario
        );
    }

    private boolean hasRequestBody(
            ApiEndpoint endpoint) {

        return endpoint.getRequestBody() != null
                && endpoint.getRequestBody()
                .getFields() != null;
    }

    private RequestPayload generateBaselinePayload(
            ApiRequestBody requestBody) {

        RequestPayload payload =
                new RequestPayload();

        for (ApiRequestBodyField field :
                requestBody.getFields()) {

            Object value =
                    generateBaselineFieldValue(
                            field
                    );

            payload.addField(
                    field.getName(),
                    value
            );
        }

        return payload;
    }

    private Object generateBaselineFieldValue(
            ApiRequestBodyField field) {

        if (field.getType() == null) {
            return null;
        }

        return switch (
                field.getType()
                        .toLowerCase()) {

            case "string" ->
                    generateBaselineStringValue(
                            field
                    );

            case "integer" -> {

                if (field.getMinimum() != null) {

                    yield field.getMinimum()
                            .intValue();
                }

                yield 1;
            }

            case "number" -> {

                if (field.getMinimum() != null) {

                    yield field.getMinimum()
                            .doubleValue();
                }

                yield 1.0;
            }

            case "boolean" ->
                    true;

            default ->
                    null;
        };
    }

    private String generateBaselineStringValue(
            ApiRequestBodyField field) {

        if (field.getPattern() == null
                || field.getPattern().isBlank()) {

            return adjustStringLength(
                    "sample-" + field.getName(),
                    field.getMinLength(),
                    field.getMaxLength()
            );
        }

        List<String> candidates =
                List.of(
                        "sample_" + field.getName(),
                        "sample",
                        "test123",
                        "abc",
                        "A1"
                );

        for (String candidate : candidates) {

            String adjustedCandidate =
                    adjustStringLength(
                            candidate,
                            field.getMinLength(),
                            field.getMaxLength()
                    );

            if (adjustedCandidate.matches(
                    field.getPattern()
            )) {

                return adjustedCandidate;
            }
        }

        return adjustStringLength(
                "a",
                field.getMinLength(),
                field.getMaxLength()
        );
    }
    private String adjustStringLength(
            String value,
            Integer minLength,
            Integer maxLength) {

        if (maxLength != null
                && maxLength == 0) {

            return "";
        }

        if (minLength != null
                && value.length() < minLength) {

            value =
                    value + "a".repeat(
                            minLength - value.length()
                    );
        }

        if (maxLength != null
                && value.length() > maxLength) {

            value =
                    value.substring(
                            0,
                            maxLength
                    );
        }

        return value;
    }

    private String findSuccessStatusCode(
            ApiEndpoint endpoint) {

        if (endpoint.getResponses() == null
                || endpoint.getResponses()
                .isEmpty()) {

            return null;
        }

        return endpoint.getResponses()
                .stream()
                .map(ApiResponse::getStatusCode)
                .filter(statusCode ->
                        statusCode != null
                                && statusCode.matches(
                                "2\\d{2}"
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private String findClientErrorStatusCode(
            ApiEndpoint endpoint) {

        if (endpoint.getResponses() == null
                || endpoint.getResponses()
                .isEmpty()) {

            return null;
        }

        return endpoint.getResponses()
                .stream()
                .map(ApiResponse::getStatusCode)
                .filter(statusCode ->
                        statusCode != null
                                && statusCode.matches(
                                "4\\d{2}"
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private void addSchemaAwareStringLengthScenarios(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            List<TestScenario> scenarios) {

        if (field.getMinLength() != null) {

            int minLength =
                    field.getMinLength();

            if (minLength > 0) {

                TestScenario belowMinimumScenario =
                        createStringPayloadScenario(
                                endpoint,
                                field,
                                "Below minimum length: "
                                        + field.getName(),
                                "Verify the endpoint rejects a string shorter than the minimum length "
                                        + "for request body field '"
                                        + field.getName()
                                        + "'",
                                "VALIDATION",
                                "a".repeat(
                                        minLength - 1
                                )
                        );

                belowMinimumScenario.setExpectedStatusCode(
                        findClientErrorStatusCode(endpoint)
                );

                scenarios.add(
                        belowMinimumScenario
                );
            }

            TestScenario minimumBoundaryScenario =
                    createStringPayloadScenario(
                            endpoint,
                            field,
                            "Minimum length: "
                                    + field.getName(),
                            "Verify the endpoint accepts a string at the minimum allowed length "
                                    + "for request body field '"
                                    + field.getName()
                                    + "'",
                            "BOUNDARY",
                            "a".repeat(
                                    minLength
                            )
                    );

            minimumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    minimumBoundaryScenario
            );
        }

        if (field.getMaxLength() != null) {

            int maxLength =
                    field.getMaxLength();

            TestScenario maximumBoundaryScenario =
                    createStringPayloadScenario(
                            endpoint,
                            field,
                            "Maximum length: "
                                    + field.getName(),
                            "Verify the endpoint accepts a string at the maximum allowed length "
                                    + "for request body field '"
                                    + field.getName()
                                    + "'",
                            "BOUNDARY",
                            "a".repeat(
                                    maxLength
                            )
                    );

            maximumBoundaryScenario.setExpectedStatusCode(
                    findSuccessStatusCode(endpoint)
            );

            scenarios.add(
                    maximumBoundaryScenario
            );

            TestScenario aboveMaximumScenario =
                    createStringPayloadScenario(
                            endpoint,
                            field,
                            "Above maximum length: "
                                    + field.getName(),
                            "Verify the endpoint rejects a string longer than the maximum length "
                                    + "for request body field '"
                                    + field.getName()
                                    + "'",
                            "VALIDATION",
                            "a".repeat(
                                    maxLength + 1
                            )
                    );

            aboveMaximumScenario.setExpectedStatusCode(
                    findClientErrorStatusCode(endpoint)
            );

            scenarios.add(
                    aboveMaximumScenario
            );
        }
    }

    private TestScenario createStringPayloadScenario(
            ApiEndpoint endpoint,
            ApiRequestBodyField field,
            String name,
            String description,
            String type,
            String value) {

        TestScenario scenario =
                new TestScenario(
                        name,
                        description,
                        type
                );

        RequestPayload payload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        payload.addField(
                field.getName(),
                value
        );

        scenario.setRequestPayload(
                payload
        );

        return scenario;
    }
}