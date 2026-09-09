package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
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

        RestClient.RequestBodySpec request =
                restClient.method(
                        org.springframework.http.HttpMethod.valueOf(
                                endpoint.getMethod()
                        )
                ).uri(endpoint.getPath());

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

            ApiResponse expectedResponse =
                    findExpectedResponse(
                            endpoint,
                            actualStatusCode
                    );

            String expectedStatusCode =
                    expectedResponse != null
                            ? expectedResponse.getStatusCode()
                            : null;

            boolean successful =
                    responseValidator.validateStatusCode(
                            actualStatusCode,
                            expectedResponse
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

    private ApiResponse findExpectedResponse(
            ApiEndpoint endpoint,
            int actualStatusCode) {

        if (endpoint.getResponses() == null) {
            return null;
        }

        return endpoint.getResponses()
                .stream()
                .filter(response ->
                        String.valueOf(actualStatusCode)
                                .equals(response.getStatusCode()))
                .findFirst()
                .orElse(null);
    }
}