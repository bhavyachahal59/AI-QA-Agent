package com.bhavyachahal.aiqa.qa;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TestReportGeneratorTest {

    @Test
    void shouldExecuteAllScenariosAndBuildReport() {

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
                .andRespond(
                        withSuccess(
                                "{\"id\":1}",
                                MediaType.APPLICATION_JSON
                        )
                );

        server.expect(
                        requestTo("/users")
                )
                .andExpect(
                        method(HttpMethod.GET)
                )
                .andRespond(
                        withSuccess(
                                "{\"id\":2}",
                                MediaType.APPLICATION_JSON
                        )
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
                                "object",
                                null
                        )
                )
        );

        TestScenario scenarioOne =
                new TestScenario(
                        "Get user 1",
                        "Execute user lookup",
                        "POSITIVE"
                );

        scenarioOne.setExpectedStatusCode("200");

        TestScenario scenarioTwo =
                new TestScenario(
                        "Get user 2",
                        "Execute another user lookup",
                        "POSITIVE"
                );

        scenarioTwo.setExpectedStatusCode("200");

        List<TestScenario> scenarios =
                List.of(
                        scenarioOne,
                        scenarioTwo
                );

        ApiTestExecutor executor =
                new ApiTestExecutor(
                        springRestClient,
                        new ResponseValidator()
                );

        TestReportGenerator reportGenerator =
                new TestReportGenerator(
                        executor
                );

        TestReport report =
                reportGenerator.executeAll(
                        endpoint,
                        scenarios
                );

        assertEquals(
                2,
                report.getTotalTests()
        );

        assertEquals(
                2,
                report.getPassedTests()
        );

        assertEquals(
                0,
                report.getFailedTests()
        );

        assertTrue(
                report.getResults()
                        .stream()
                        .allMatch(TestExecutionResult::isSuccessful)
        );

        server.verify();
    }
}