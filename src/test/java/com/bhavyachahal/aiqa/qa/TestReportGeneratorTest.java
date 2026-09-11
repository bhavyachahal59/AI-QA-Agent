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

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Test
    void shouldCalculatePassRate() {

        TestReport report =
                new TestReport();

        report.addResult(
                new TestExecutionResult(
                        "Scenario 1",
                        200,
                        "200",
                        "{}",
                        true
                )
        );

        report.addResult(
                new TestExecutionResult(
                        "Scenario 2",
                        200,
                        "200",
                        "{}",
                        true
                )
        );

        report.addResult(
                new TestExecutionResult(
                        "Scenario 3",
                        500,
                        "200",
                        "{}",
                        false
                )
        );

        report.addResult(
                new TestExecutionResult(
                        "Scenario 4",
                        400,
                        "400",
                        "{}",
                        true
                )
        );

        assertEquals(
                75.0,
                report.getPassRate()
        );
    }

    @Test
    void shouldReturnZeroPassRateWhenNoTestsExist() {

        TestReport report =
                new TestReport();

        assertEquals(
                0.0,
                report.getPassRate()
        );

        assertEquals(
                0,
                report.getTotalTests()
        );

        assertEquals(
                0,
                report.getPassedTests()
        );

        assertEquals(
                0,
                report.getFailedTests()
        );
    }

    @Test
    void shouldGenerateHumanReadableReportSummary() {

        TestReport report =
                new TestReport();

        report.addResult(
                new TestExecutionResult(
                        "Valid request",
                        200,
                        "200",
                        "{\"id\":1}",
                        true
                )
        );

        report.addResult(
                new TestExecutionResult(
                        "Missing required parameter: username",
                        200,
                        "400",
                        "{\"id\":1}",
                        false
                )
        );

        String summary =
                report.toSummary();

        assertTrue(
                summary.contains("API Test Report")
        );

        assertTrue(
                summary.contains("Total: 2")
        );

        assertTrue(
                summary.contains("Passed: 1")
        );

        assertTrue(
                summary.contains("Failed: 1")
        );

        assertTrue(
                summary.contains("Pass rate: 50.0%")
        );

        assertTrue(
                summary.contains(
                        "PASS | Valid request"
                )
        );

        assertTrue(
                summary.contains(
                        "FAIL | Missing required parameter: username"
                )
        );

        assertTrue(
                summary.contains("Expected: 400")
        );

        assertTrue(
                summary.contains("Actual: 200")
        );
    }

    @Test
    void shouldGenerateMarkdownReport() {

        TestReport report =
                new TestReport();

        report.addResult(
                new TestExecutionResult(
                        "Valid request",
                        200,
                        "200",
                        "{\"id\":1}",
                        true
                )
        );

        report.addResult(
                new TestExecutionResult(
                        "Missing required parameter: username",
                        200,
                        "400",
                        "{\"id\":1}",
                        false
                )
        );

        String markdown =
                report.toMarkdown();

        assertTrue(
                markdown.contains("# API Test Report")
        );

        assertTrue(
                markdown.contains("## Summary")
        );

        assertTrue(
                markdown.contains("- Total: 2")
        );

        assertTrue(
                markdown.contains("- Passed: 1")
        );

        assertTrue(
                markdown.contains("- Failed: 1")
        );

        assertTrue(
                markdown.contains("- Pass rate: 50.0%")
        );

        assertTrue(
                markdown.contains("## Results")
        );

        assertTrue(
                markdown.contains(
                        "### PASS — Valid request"
                )
        );

        assertTrue(
                markdown.contains(
                        "### FAIL — Missing required parameter: username"
                )
        );

        assertTrue(
                markdown.contains("- Expected status: 400")
        );

        assertTrue(
                markdown.contains("- Actual status: 200")
        );
    }

    @Test
    void shouldWriteMarkdownReportToFile() throws Exception {

        TestReport report =
                new TestReport();

        report.addResult(
                new TestExecutionResult(
                        "Valid request",
                        200,
                        "200",
                        "{\"id\":1}",
                        true
                )
        );

        Path outputPath =
                Files.createTempFile(
                        "api-test-report",
                        ".md"
                );

        report.writeMarkdown(outputPath);

        String content =
                Files.readString(outputPath);

        assertTrue(
                content.contains("# API Test Report")
        );

        assertTrue(
                content.contains("### PASS — Valid request")
        );

        Files.deleteIfExists(outputPath);
    }
}