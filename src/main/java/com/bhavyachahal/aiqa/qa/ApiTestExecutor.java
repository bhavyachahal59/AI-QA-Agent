package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;

import org.springframework.web.client.RestClient;

public class ApiTestExecutor {

    private final RestClient restClient;
    private final ResponseValidator responseValidator;

    public ApiTestExecutor(
            RestClient restClient,
            ResponseValidator responseValidator) {

        this.restClient = restClient;
        this.responseValidator = responseValidator;
    }

    public TestExecutionResult execute(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        String uri =
                buildUri(
                        endpoint,
                        scenario
                );

        RestClient.RequestBodySpec request =
                restClient.method(
                        org.springframework.http.HttpMethod.valueOf(
                                endpoint.getMethod()
                        )
                ).uri(uri);

        if (endpoint.getParameters() != null) {

            for (ApiParameter parameter :
                    endpoint.getParameters()) {

                if (!"header".equalsIgnoreCase(
                        parameter.getLocation())) {

                    continue;
                }

                Object value =
                        scenario.getParameterValues()
                                .get(parameter.getName());

                if (value != null) {

                    request.header(
                            parameter.getName(),
                            String.valueOf(value)
                    );
                }
            }
        }

        if (scenario.getRequestPayload() != null) {

            request.body(
                    scenario.getRequestPayload().getFields()
            );
        }

        return request.exchange((clientRequest, clientResponse) -> {

            String responseBody =
                    clientResponse.bodyTo(String.class);

            int actualStatusCode =
                    clientResponse.getStatusCode().value();

            String expectedStatusCode =
                    scenario.getExpectedStatusCode();

            boolean successful =
                    responseValidator.validateStatusCode(
                            actualStatusCode,
                            expectedStatusCode
                    );

            return new TestExecutionResult(
                    scenario.getName(),
                    actualStatusCode,
                    expectedStatusCode,
                    responseBody,
                    successful
            );
        });
    }

    private String buildUri(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        String uri = endpoint.getPath();

        if (endpoint.getParameters() == null) {
            return uri;
        }

        for (ApiParameter parameter :
                endpoint.getParameters()) {

            Object value =
                    scenario.getParameterValues()
                            .get(parameter.getName());

            if (value == null) {
                continue;
            }

            if ("path".equalsIgnoreCase(
                    parameter.getLocation())) {

                uri = uri.replace(
                        "{" + parameter.getName() + "}",
                        String.valueOf(value)
                );
            }

            if ("query".equalsIgnoreCase(
                    parameter.getLocation())) {

                String separator =
                        uri.contains("?") ? "&" : "?";

                uri = uri
                        + separator
                        + parameter.getName()
                        + "="
                        + String.valueOf(value);
            }
        }

        return uri;
    }
}