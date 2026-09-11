package com.bhavyachahal.aiqa.controller;

import com.bhavyachahal.aiqa.qa.ApiTestService;
import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiSpecification;
import com.bhavyachahal.aiqa.specification.parser.OpenApiSpecificationParser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QaController.class)
class QaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestScenarioGenerator scenarioGenerator;

    @MockitoBean
    private OpenApiSpecificationParser specificationParser;

    @MockitoBean
    private ApiTestService apiTestService;

    @Test
    void shouldReturnHealthStatus() throws Exception {

        mockMvc.perform(
                        get("/api/qa/health")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content().string(
                                "AI QA Agent is running"
                        )
                );
    }

    @Test
    void shouldGenerateScenarios() throws Exception {

        TestScenario scenario =
                new TestScenario();

        scenario.setName(
                "Valid request"
        );

        scenario.setType(
                "POSITIVE"
        );

        scenario.setExpectedStatusCode(
                "200"
        );

        when(
                scenarioGenerator.generate(
                        any()
                )
        ).thenReturn(
                List.of(scenario)
        );

        String requestBody = """
                {
                  "path": "/users",
                  "method": "GET"
                }
                """;

        mockMvc.perform(
                        post("/api/qa/scenarios")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value(
                                        "Valid request"
                                )
                )
                .andExpect(
                        jsonPath("$[0].type")
                                .value(
                                        "POSITIVE"
                                )
                )
                .andExpect(
                        jsonPath("$[0].expectedStatusCode")
                                .value(
                                        "200"
                                )
                );
    }

    @Test
    void shouldGenerateScenariosFromOpenApi()
            throws Exception {

        UUID specificationId =
                UUID.randomUUID();

        ApiSpecification specification =
                new ApiSpecification(
                        specificationId,
                        "User API",
                        "1.0.0",
                        "openapi-content",
                        "yaml",
                        Instant.now()
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath(
                "/users"
        );

        endpoint.setMethod(
                "GET"
        );

        TestScenario scenario =
                new TestScenario();

        scenario.setName(
                "Valid request"
        );

        scenario.setType(
                "POSITIVE"
        );

        scenario.setExpectedStatusCode(
                "200"
        );

        when(
                specificationParser.parse(
                        anyString(),
                        anyString()
                )
        ).thenReturn(
                specification
        );

        when(
                specificationParser.parseEndpoints(
                        anyString(),
                        any(UUID.class)
                )
        ).thenReturn(
                List.of(endpoint)
        );

        when(
                scenarioGenerator.generate(
                        any(ApiEndpoint.class)
                )
        ).thenReturn(
                List.of(scenario)
        );

        String requestBody = """
                {
                  "content": "openapi: 3.0.0",
                  "format": "yaml"
                }
                """;

        mockMvc.perform(
                        post("/api/qa/openapi/scenarios")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value(
                                        "Valid request"
                                )
                )
                .andExpect(
                        jsonPath("$[0].type")
                                .value(
                                        "POSITIVE"
                                )
                )
                .andExpect(
                        jsonPath("$[0].expectedStatusCode")
                                .value(
                                        "200"
                                )
                );
    }

    @Test
    void shouldExecuteOpenApiSpecification()
            throws Exception {

        UUID specificationId =
                UUID.randomUUID();

        ApiSpecification specification =
                new ApiSpecification(
                        specificationId,
                        "User API",
                        "1.0.0",
                        "openapi-content",
                        "yaml",
                        Instant.now()
                );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath(
                "/users"
        );

        endpoint.setMethod(
                "GET"
        );

        TestExecutionResult result =
                new TestExecutionResult(
                        "Valid request",
                        200,
                        "200",
                        "{}",
                        true
                );

        when(
                specificationParser.parse(
                        anyString(),
                        anyString()
                )
        ).thenReturn(
                specification
        );

        when(
                specificationParser.parseEndpoints(
                        anyString(),
                        any(UUID.class)
                )
        ).thenReturn(
                List.of(endpoint)
        );

        when(
                apiTestService.executeEndpoint(
                        any(ApiEndpoint.class),
                        anyString()
                )
        ).thenReturn(
                List.of(result)
        );

        String requestBody = """
                {
                  "baseUrl": "https://api.example.com",
                  "content": "openapi: 3.0.0",
                  "format": "yaml"
                }
                """;

        mockMvc.perform(
                        post("/api/qa/openapi/execute")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.totalTests")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.passedTests")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.failedTests")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.passRate")
                                .value(100.0)
                )
                .andExpect(
                        jsonPath("$.results[0].scenarioName")
                                .value(
                                        "Valid request"
                                )
                )
                .andExpect(
                        jsonPath("$.results[0].actualStatusCode")
                                .value(200)
                )
                .andExpect(
                        jsonPath("$.results[0].successful")
                                .value(true)
                );
    }
}