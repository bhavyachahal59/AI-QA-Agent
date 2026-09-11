package com.bhavyachahal.aiqa;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment =
                SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
        classes = {
                AiQaAgentApplication.class,
                OpenApiExecutionEndToEndTest.TargetApiController.class
        }
)
class OpenApiExecutionEndToEndTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldExecuteOpenApiSpecificationEndToEnd()
            throws Exception {

        String port =
                environment.getProperty(
                        "local.server.port"
                );

        assertNotNull(port);

        String baseUrl =
                "http://localhost:" + port;

        String openApiContent =
                Files.readString(
                        Path.of(
                                "src/test/resources/e2e-api.yaml"
                        )
                );

        Map<String, Object> requestBody =
                new LinkedHashMap<>();

        requestBody.put(
                "baseUrl",
                baseUrl
        );

        requestBody.put(
                "content",
                openApiContent
        );

        requestBody.put(
                "format",
                "yaml"
        );

        RestClient client =
                RestClient.create();

        String responseBody =
                client.post()
                        .uri(
                                baseUrl
                                        + "/api/qa/openapi/execute"
                        )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .body(
                                requestBody
                        )
                        .retrieve()
                        .body(
                                String.class
                        );

        assertNotNull(
                responseBody
        );

        assertTrue(
                responseBody.contains(
                        "\"failedTests\":0"
                )
        );

        assertTrue(
                responseBody.contains(
                        "\"passRate\":100.0"
                )
        );

        assertTrue(
                responseBody.contains(
                        "\"actualStatusCode\":200"
                )
        );

        assertTrue(
                responseBody.contains(
                        "\"successful\":true"
                )
        );
    }

    @RestController
    static class TargetApiController {

        @GetMapping("/e2e/ping")
        ResponseEntity<String> ping() {

            return ResponseEntity.ok(
                    "pong"
            );
        }
    }
}