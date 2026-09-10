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
    void shouldReplacePathParameter() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users/123")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":123,\"name\":\"Bhavya\"}",
                                org.springframework.http.MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");

        endpoint.setResponses(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                )
        );

        com.bhavyachahal.aiqa.specification.model.ApiParameter parameter =
                new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                        "id",
                        "path",
                        true,
                        "integer"
                );

        endpoint.setParameters(
                java.util.List.of(parameter)
        );

        TestScenario scenario =
                new TestScenario(
                        "Valid user lookup",
                        "Execute user lookup with valid ID",
                        "POSITIVE"
                );

        scenario.addParameterValue(
                "id",
                123
        );

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
                200,
                result.getActualStatusCode()
        );

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }

    @Test
    void shouldAddQueryParameter() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users?status=active")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withSuccess(
                                "[{\"id\":123,\"name\":\"Bhavya\"}]",
                                org.springframework.http.MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        endpoint.setResponses(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "array",
                                null
                        )
                )
        );

        com.bhavyachahal.aiqa.specification.model.ApiParameter parameter =
                new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                        "status",
                        "query",
                        true,
                        "string"
                );

        endpoint.setParameters(
                java.util.List.of(parameter)
        );

        TestScenario scenario =
                new TestScenario(
                        "Filter active users",
                        "Execute user search with active status",
                        "POSITIVE"
                );

        scenario.addParameterValue(
                "status",
                "active"
        );

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
                200,
                result.getActualStatusCode()
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
        scenario.setExpectedStatusCode("200");

        scenario.setRequestPayload(payload);

        return scenario;
    }

    @Test
    void shouldAddHeaderParameter() {

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
                        method(HttpMethod.GET)
                )
                .andExpect(
                        org.springframework.test.web.client.match.MockRestRequestMatchers
                                .header(
                                        "X-API-Key",
                                        "abc123"
                                )
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":123,\"name\":\"Bhavya\"}",
                                org.springframework.http.MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        endpoint.setResponses(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                )
        );

        com.bhavyachahal.aiqa.specification.model.ApiParameter parameter =
                new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                        "X-API-Key",
                        "header",
                        true,
                        "string"
                );

        endpoint.setParameters(
                java.util.List.of(parameter)
        );

        TestScenario scenario =
                new TestScenario(
                        "Authenticated user lookup",
                        "Execute user lookup with API key",
                        "POSITIVE"
                );

        scenario.addParameterValue(
                "X-API-Key",
                "abc123"
        );

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
                200,
                result.getActualStatusCode()
        );

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }

    @Test
    void shouldExecuteScenarioWithQueryParameter() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(restClientBuilder)
                        .build();

        RestClient springRestClient =
                restClientBuilder.build();

        server.expect(
                        requestTo("/users?limit=10")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":1,\"name\":\"Bhavya\"}",
                                org.springframework.http.MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");

        endpoint.setMethod("GET");

        endpoint.setParameters(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                                "limit",
                                "query",
                                true,
                                "integer"
                        )
                )
        );

        endpoint.setResponses(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                )
        );

        TestScenario scenario =
                new TestScenario(
                        "Query parameter request",
                        "Execute request with query parameter",
                        "POSITIVE"
                );
        scenario.setExpectedStatusCode("200");

        scenario.addParameterValue(
                "limit",
                10
        );

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
                "Query parameter request",
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

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }

    @Test
    void shouldExecuteScenarioWithHeaderParameter() {

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
                        method(HttpMethod.GET)
                )
                .andExpect(
                        org.springframework.test.web.client.match.MockRestRequestMatchers.header(
                                "X-API-Key",
                                "test-key"
                        )
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":1,\"name\":\"Bhavya\"}",
                                org.springframework.http.MediaType.APPLICATION_JSON
                        )
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");

        endpoint.setMethod("GET");

        endpoint.setParameters(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiParameter(
                                "X-API-Key",
                                "header",
                                true,
                                "string"
                        )
                )
        );

        endpoint.setResponses(
                java.util.List.of(
                        new com.bhavyachahal.aiqa.specification.model.ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                "object",
                                null
                        )
                )
        );

        TestScenario scenario =
                new TestScenario(
                        "Header parameter request",
                        "Execute request with API key header",
                        "POSITIVE"
                );
        scenario.setExpectedStatusCode("200");

        scenario.addParameterValue(
                "X-API-Key",
                "test-key"
        );

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
                "Header parameter request",
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

        assertTrue(
                result.isSuccessful()
        );

        server.verify();
    }
}