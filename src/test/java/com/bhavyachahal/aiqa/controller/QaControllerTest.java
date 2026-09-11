package com.bhavyachahal.aiqa.controller;

import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestScenario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
}