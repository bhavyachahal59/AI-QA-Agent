package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ApiTestExecutorTest {

    @Test
    void shouldExecuteGeneratedScenario() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":1,\"name\":\"Bhavya\"}",
                                MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                createEndpoint(
                        new ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                );

        TestScenario scenario =
                createScenario();

        ApiTestExecutor executor =
                new ApiTestExecutor(
                        springRestClient,
                        new ResponseValidator()
                );

        TestExecutionResult result =
                executor.execute(
                        endpoint,
                        scenario
                );

        assertEquals(
                "Valid request",
                result.getScenarioName()
        );

        assertEquals(
                200,
                result.getActualStatusCode()
        );

        assertEquals(
                "200",
                result.getExpectedStatusCode()
        );

        assertEquals(
                "{\"id\":1,\"name\":\"Bhavya\"}",
                result.getResponseBody()
        );

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }

    @Test
    void shouldValidateDocumentedBadRequestResponse() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withStatus(BAD_REQUEST)
                );

        ApiEndpoint endpoint =
                createEndpoint(
                        new ApiResponse(
                                "400",
                                "Bad request",
                                "application/json",
                                "object",
                                null
                        )
                );

        TestScenario scenario =
                createScenario();

        ApiTestExecutor executor =
                new ApiTestExecutor(
                        springRestClient,
                        new ResponseValidator()
                );

        TestExecutionResult result =
                executor.execute(
                        endpoint,
                        scenario
                );

        assertEquals(
                400,
                result.getActualStatusCode()
        );

        assertEquals(
                "400",
                result.getExpectedStatusCode()
        );

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }

    @Test
    void shouldFailWhenResponseIsNotDocumented() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withStatus(
                                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        ApiEndpoint endpoint =
                createEndpoint(
                        new ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                );

        TestScenario scenario =
                createScenario();

        ApiTestExecutor executor =
                new ApiTestExecutor(
                        springRestClient,
                        new ResponseValidator()
                );

        TestExecutionResult result =
                executor.execute(
                        endpoint,
                        scenario
                );

        assertEquals(
                500,
                result.getActualStatusCode()
        );

        assertEquals(
                null,
                result.getExpectedStatusCode()
        );

        assertFalse(
                result.isSuccessful()
        );

        server.verify();
    }

    private ApiEndpoint createEndpoint(
            ApiResponse... responses) {

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("POST");
        endpoint.setResponses(
                List.of(responses)
        );

        return endpoint;
    }

    private TestScenario createScenario() {

        RequestPayload payload =
                new RequestPayload();

        payload.setFields(
                Map.of(
                        "name",
                        "Bhavya",
                        "email",
                        "bhavya@example.com"
                )
        );

        TestScenario scenario =
                new TestScenario(
                        "Valid request",
                        "Execute valid request",
                        "POSITIVE"
                );

        scenario.setRequestPayload(payload);

        return scenario;
    }
}