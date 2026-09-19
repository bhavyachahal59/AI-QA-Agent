package com.bhavyachahal.aiqa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment =
                SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "ai.enabled=true"
)
@ContextConfiguration(
        classes = {
                AiQaAgentApplication.class,
                AiOpenApiExecutionEndToEndTest.PaymentController.class
        }
)
@EnabledIfEnvironmentVariable(
        named = "RUN_OPENAI_INTEGRATION_TESTS",
        matches = "true"
)
class AiOpenApiExecutionEndToEndTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldGenerateExecuteAndReportAiScenarioEndToEnd() {

        String port =
                environment.getProperty(
                        "local.server.port"
                );

        assertNotNull(port);

        String baseUrl =
                "http://localhost:" + port;

        String openApiContent =
                """
                openapi: 3.0.3

                info:
                  title: AI Payment E2E API
                  version: 1.0.0

                paths:
                  /e2e/ai/payments:
                    post:
                      summary: Transfer money between accounts

                      requestBody:
                        required: true
                        content:
                          application/json:
                            schema:
                              type: object
                              required:
                                - sourceAccountId
                                - destinationAccountId
                                - amount

                              properties:
                                sourceAccountId:
                                  type: string

                                destinationAccountId:
                                  type: string

                                amount:
                                  type: integer
                                  minimum: 1

                      responses:
                        "201":
                          description: Payment created

                        "400":
                          description: Invalid payment

                        "409":
                          description: Conflicting payment
                """;

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

        System.out.println(
                "AI END-TO-END REPORT | "
                        + responseBody
        );

        /*
         * At least one AI semantic scenario should be executed
         * without a resolvable expected HTTP status.
         *
         * Because both 400 and 409 are documented rejection
         * responses, that execution must remain UNVERIFIED.
         */
        assertTrue(
                responseBody.contains(
                        "\"unverifiedTests\""
                )
        );

        assertTrue(
                responseBody.matches(
                        ".*\"unverifiedTests\"\\s*:\\s*[1-9][0-9]*.*"
                )
        );

        /*
         * Recommendations must never be executed, so they should
         * never appear as execution results.
         */
        assertTrue(
                !responseBody.contains(
                        "insufficient available funds"
                )
        );
    }

    @RestController
    static class PaymentController {

        @PostMapping("/e2e/ai/payments")
        ResponseEntity<String> createPayment(
                @RequestBody
                Map<String, Object> request) {

            Object sourceAccountId =
                    request.get(
                            "sourceAccountId"
                    );

            Object destinationAccountId =
                    request.get(
                            "destinationAccountId"
                    );

            Object amount =
                    request.get(
                            "amount"
                    );

            if (sourceAccountId == null
                    || destinationAccountId == null
                    || amount == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Missing required payment data"
                        );
            }

            if (!(amount instanceof Number)) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Amount must be numeric"
                        );
            }

            if (((Number) amount).longValue() < 1) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Amount must be positive"
                        );
            }

            if (sourceAccountId.equals(
                    destinationAccountId
            )) {

                return ResponseEntity
                        .status(
                                HttpStatus.BAD_REQUEST
                        )
                        .body(
                                "Source and destination accounts must differ"
                        );
            }

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED
                    )
                    .body(
                            "Payment created"
                    );
        }
    }
}