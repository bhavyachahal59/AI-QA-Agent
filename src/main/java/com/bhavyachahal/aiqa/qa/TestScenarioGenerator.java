package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class TestScenarioGenerator {

    public List<TestScenario> generate(ApiEndpoint endpoint) {

        List<TestScenario> scenarios = new ArrayList<>();

        generateParameterScenarios(endpoint, scenarios);

        TestScenario validRequestScenario =
                new TestScenario(
                        "Valid request",
                        "Verify the endpoint accepts a valid request",
                        "POSITIVE"
                );

        if (hasRequestBody(endpoint)) {

            validRequestScenario.setRequestPayload(
                    generateBaselinePayload(endpoint.getRequestBody())
            );
        }

        addBaselineParameterValues(
                endpoint,
                validRequestScenario
        );

        String successStatusCode =
                findSuccessStatusCode(endpoint);

        validRequestScenario.setExpectedStatusCode(
                successStatusCode
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
                    successStatusCode
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

                    scenarios.add(invalidScenario);

                    if ("integer".equalsIgnoreCase(parameter.getType())) {

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
                    }

                    if ("string".equalsIgnoreCase(parameter.getType())) {

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
                });
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
                                generateBaselineParameterValue(parameter)
                        )
                );
    }

    private Object generateBaselineParameterValue(
            ApiParameter parameter) {

        if ("integer".equalsIgnoreCase(parameter.getType())) {
            return 1;
        }

        if ("number".equalsIgnoreCase(parameter.getType())) {
            return 1.0;
        }

        if ("boolean".equalsIgnoreCase(parameter.getType())) {
            return true;
        }

        return "sample-" + parameter.getName();
    }

    private Object generateInvalidParameterValue(
            ApiParameter parameter) {

        if ("integer".equalsIgnoreCase(parameter.getType())) {
            return "not-an-integer";
        }

        if ("number".equalsIgnoreCase(parameter.getType())) {
            return "not-a-number";
        }

        if ("boolean".equalsIgnoreCase(parameter.getType())) {
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

                    payload.getFields().remove(
                            field.getName()
                    );

                    scenario.setRequestPayload(
                            payload
                    );

                    scenarios.add(
                            scenario
                    );
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

                        scenarios.add(
                                scenario
                        );
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

                        TestScenario scenario =
                                new TestScenario(
                                        "Empty string: "
                                                + field.getName(),
                                        "Verify the endpoint validates an empty string "
                                                + "for request body field '"
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
                                ""
                        );

                        scenario.setRequestPayload(
                                payload
                        );

                        scenarios.add(
                                scenario
                        );
                    }
                });
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

        scenarios.add(
                invalidScenario
        );

        TestScenario negativeScenario =
                new TestScenario(
                        "Negative integer: "
                                + field.getName(),
                        "Verify the endpoint handles a negative integer "
                                + "value for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY"
                );

        RequestPayload negativePayload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        negativePayload.addField(
                field.getName(),
                -1
        );

        negativeScenario.setRequestPayload(
                negativePayload
        );

        scenarios.add(
                negativeScenario
        );

        TestScenario zeroScenario =
                new TestScenario(
                        "Zero value: "
                                + field.getName(),
                        "Verify the endpoint handles a zero value "
                                + "for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY"
                );

        RequestPayload zeroPayload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        zeroPayload.addField(
                field.getName(),
                0
        );

        zeroScenario.setRequestPayload(
                zeroPayload
        );

        scenarios.add(
                zeroScenario
        );

        TestScenario largeScenario =
                new TestScenario(
                        "Large integer: "
                                + field.getName(),
                        "Verify the endpoint handles a large integer "
                                + "value for request body field '"
                                + field.getName()
                                + "'",
                        "BOUNDARY"
                );

        RequestPayload largePayload =
                generateBaselinePayload(
                        endpoint.getRequestBody()
                ).copy();

        largePayload.addField(
                field.getName(),
                Integer.MAX_VALUE
        );

        largeScenario.setRequestPayload(
                largePayload
        );

        scenarios.add(
                largeScenario
        );
    }

    private boolean hasRequestBody(
            ApiEndpoint endpoint) {

        return endpoint.getRequestBody() != null
                && endpoint.getRequestBody().getFields() != null;
    }

    private RequestPayload generateBaselinePayload(
            ApiRequestBody requestBody) {

        RequestPayload payload =
                new RequestPayload();

        for (ApiRequestBodyField field :
                requestBody.getFields()) {

            Object value =
                    switch (field.getType().toLowerCase()) {

                        case "string" ->
                                "sample-" + field.getName();

                        case "integer" ->
                                1;

                        case "number" ->
                                1.0;

                        case "boolean" ->
                                true;

                        default ->
                                null;
                    };

            payload.addField(
                    field.getName(),
                    value
            );
        }

        return payload;
    }

    private String findSuccessStatusCode(
            ApiEndpoint endpoint) {

        if (endpoint.getResponses() == null
                || endpoint.getResponses().isEmpty()) {

            return null;
        }

        return endpoint.getResponses()
                .stream()
                .map(ApiResponse::getStatusCode)
                .filter(statusCode ->
                        statusCode != null
                                && statusCode.matches("2\\d{2}")
                )
                .findFirst()
                .orElse(null);
    }
}