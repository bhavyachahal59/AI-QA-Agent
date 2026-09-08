package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;

import java.util.ArrayList;
import java.util.List;

public class TestScenarioGenerator {
    public List<TestScenario> generate(ApiEndpoint endpoint) {

        List<TestScenario> scenarios = new ArrayList<>();

        scenarios.add(
                new TestScenario(
                        "Valid request",
                        "Verify the endpoint accepts a valid request",
                        "POSITIVE"
                )
        );

        if (endpoint.getParameters() != null) {

            endpoint.getParameters()
                    .stream()
                    .filter(parameter -> parameter.isRequired())
                    .forEach(parameter -> {

                        scenarios.add(
                                new TestScenario(
                                        "Missing required parameter: "
                                                + parameter.getName(),
                                        "Verify the endpoint rejects a request when the required "
                                                + parameter.getLocation()
                                                + " parameter '"
                                                + parameter.getName()
                                                + "' is missing",
                                        "VALIDATION"
                                )
                        );

                        scenarios.add(
                                new TestScenario(
                                        "Invalid parameter: "
                                                + parameter.getName(),
                                        "Verify the endpoint handles an invalid value for the "
                                                + parameter.getLocation()
                                                + " parameter '"
                                                + parameter.getName()
                                                + "'",
                                        "NEGATIVE"
                                )
                        );

                        if ("integer".equalsIgnoreCase(parameter.getType())) {

                            scenarios.add(
                                    new TestScenario(
                                            "Boundary value: " + parameter.getName(),
                                            "Verify the endpoint handles boundary integer values for the "
                                                    + parameter.getLocation()
                                                    + " parameter '"
                                                    + parameter.getName()
                                                    + "'",
                                            "BOUNDARY"
                                    )
                            );
                        }

                        if ("string".equalsIgnoreCase(parameter.getType())) {

                            scenarios.add(
                                    new TestScenario(
                                            "Empty value: " + parameter.getName(),
                                            "Verify the endpoint handles an empty string for the "
                                                    + parameter.getLocation()
                                                    + " parameter '"
                                                    + parameter.getName()
                                                    + "'",
                                            "BOUNDARY"
                                    )
                            );
                        }
                    });
        }

        if (endpoint.getRequestBody() != null) {

            scenarios.add(
                    new TestScenario(
                            "Invalid request body",
                            "Verify the endpoint rejects an invalid request body",
                            "NEGATIVE"
                    )
            );
        }

        if (endpoint.getRequestBody() != null
                && endpoint.getRequestBody().getFields() != null) {

            endpoint.getRequestBody()
                    .getFields()
                    .stream()
                    .filter(ApiRequestBodyField::isRequired)
                    .forEach(field -> {

                        scenarios.add(
                                new TestScenario(
                                        "Missing required field: "
                                                + field.getName(),
                                        "Verify the endpoint rejects the request when required "
                                                + "request body field '"
                                                + field.getName()
                                                + "' is missing",
                                        "VALIDATION"
                                )
                        );
                    });
        }

        if (endpoint.getRequestBody() != null
                && endpoint.getRequestBody().getFields() != null) {

            endpoint.getRequestBody()
                    .getFields()
                    .forEach(field -> {

                        if ("email".equalsIgnoreCase(field.getFormat())) {

                            scenarios.add(
                                    new TestScenario(
                                            "Invalid email: " + field.getName(),
                                            "Verify the endpoint rejects an invalid email "
                                                    + "value for request body field '"
                                                    + field.getName()
                                                    + "'",
                                            "VALIDATION"
                                    )
                            );
                        }

                        if ("integer".equalsIgnoreCase(field.getType())) {

                            scenarios.add(
                                    new TestScenario(
                                            "Invalid integer: " + field.getName(),
                                            "Verify the endpoint rejects a non-integer "
                                                    + "value for request body field '"
                                                    + field.getName()
                                                    + "'",
                                            "VALIDATION"
                                    )
                            );
                        }

                        if ("string".equalsIgnoreCase(field.getType())
                                && !"email".equalsIgnoreCase(field.getFormat())) {

                            scenarios.add(
                                    new TestScenario(
                                            "Empty string: " + field.getName(),
                                            "Verify the endpoint validates an empty string "
                                                    + "for request body field '"
                                                    + field.getName()
                                                    + "'",
                                            "VALIDATION"
                                    )
                            );
                        }
                    });
        }

        if (endpoint.getResponses() != null
                && !endpoint.getResponses().isEmpty()) {

            scenarios.add(
                    new TestScenario(
                            "Expected response",
                            "Verify the endpoint returns an expected response",
                            "POSITIVE"
                    )
            );
        }

        return scenarios;
    }
}