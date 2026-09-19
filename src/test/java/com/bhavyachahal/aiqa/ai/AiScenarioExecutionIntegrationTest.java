package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.ApiTestExecutor;
import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestExecutionResult;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(
        webEnvironment =
                SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
        classes = {
                com.bhavyachahal.aiqa.AiQaAgentApplication.class,
                AiScenarioExecutionIntegrationTest.PaymentController.class
        }
)
class AiScenarioExecutionIntegrationTest {

    @Autowired
    private Environment environment;

    @Autowired
    private ApiTestExecutor testExecutor;

    @Test
    void shouldExecuteAiScenarioAndKeepAmbiguousResultUnverified() {

        String port =
                environment.getProperty(
                        "local.server.port"
                );

        String baseUrl =
                "http://localhost:" + port;

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setPath(
                "/e2e/payments"
        );

        endpoint.setMethod(
                "POST"
        );

        endpoint.setResponses(
                List.of(
                        new ApiResponse(
                                "201",
                                "Payment created",
                                "application/json",
                                null,
                                null
                        ),
                        new ApiResponse(
                                "400",
                                "Invalid payment",
                                "application/json",
                                null,
                                null
                        ),
                        new ApiResponse(
                                "409",
                                "Conflicting payment",
                                "application/json",
                                null,
                                null
                        )
                )
        );

        TestScenario scenario =
                new TestScenario(
                        "Reject transfer to the same account",
                        "Source and destination accounts must differ",
                        "AI_EXECUTABLE"
                );

        scenario.setExpectedOutcome(
                "REJECT"
        );

        // Deliberately null because REJECT is ambiguous:
        // the contract documents both 400 and 409.
        scenario.setExpectedStatusCode(
                null
        );

        RequestPayload payload =
                new RequestPayload();

        payload.addField(
                "sourceAccountId",
                "account-001"
        );

        payload.addField(
                "destinationAccountId",
                "account-001"
        );

        payload.addField(
                "amount",
                100
        );

        scenario.setRequestPayload(
                payload
        );

        TestExecutionResult result =
                testExecutor.execute(
                        endpoint,
                        scenario,
                        baseUrl
                );

        assertEquals(
                "Reject transfer to the same account",
                result.getScenarioName()
        );

        assertEquals(
                400,
                result.getActualStatusCode()
        );

        assertNull(
                result.getExpectedStatusCode()
        );

        assertFalse(
                result.isVerified()
        );

        assertFalse(
                result.isSuccessful()
        );

        System.out.println(
                "AI EXECUTION | "
                        + result.getScenarioName()
                        + " | actual="
                        + result.getActualStatusCode()
                        + " | expected="
                        + result.getExpectedStatusCode()
                        + " | verified="
                        + result.isVerified()
                        + " | successful="
                        + result.isSuccessful()
        );
    }

    @RestController
    static class PaymentController {

        @PostMapping("/e2e/payments")
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

            if (sourceAccountId != null
                    && sourceAccountId.equals(
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