package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class ApiTestExecutorTest {

    @Test
    void shouldReportExpectedAndActualStatusWhenRequestFails() {

        RestClient.Builder restClientBuilder =
                RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer
                        .bindTo(restClientBuilder)
                        .build();

        RestClient restClient =
                restClientBuilder.build();

        ApiTestExecutor executor =
                new ApiTestExecutor(
                        restClient,
                        new ResponseValidator()
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        endpoint.setResponses(
                List.of(
                        new ApiResponse(
                                "200",
                                "Successful response",
                                "application/json",
                                null,
                                null
                        )
                )
        );

        TestScenario scenario =
                new TestScenario(
                        "Valid request",
                        "Verify the endpoint accepts a valid request",
                        "POSITIVE"
                );

        scenario.setExpectedStatusCode("200");

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withStatus(INTERNAL_SERVER_ERROR)
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
                500,
                result.getActualStatusCode()
        );

        assertEquals(
                "200",
                result.getExpectedStatusCode()
        );

        assertFalse(
                result.isSuccessful()
        );

        server.verify();
    }
}