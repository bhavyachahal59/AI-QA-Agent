package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ApiTestServiceTest {

    @Test
    void shouldGenerateAndExecuteAllScenarios() {

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

        TestScenarioGenerator generator =
                new TestScenarioGenerator();

        ApiTestService service =
                new ApiTestService(
                        generator,
                        executor
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withSuccess()
                );

        List<TestExecutionResult> results =
                service.executeEndpoint(endpoint);

        assertEquals(
                1,
                results.size()
        );

        server.verify();
    }
}