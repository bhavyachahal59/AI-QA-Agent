package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBody;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiScenarioSchemaFilterTest {

    private final AiScenarioSchemaFilter filter =
            new AiScenarioSchemaFilter();

    @Test
    void shouldRemoveAiExecutableScenarioBelowSchemaMinimum() {

        ApiEndpoint endpoint =
                paymentEndpoint();

        TestScenario scenario =
                new TestScenario(
                        "Reject non-positive amount",
                        "Reject amount below minimum",
                        "AI_EXECUTABLE"
                );

        RequestPayload payload =
                new RequestPayload();

        payload.addField(
                "sourceAccountId",
                "account-1"
        );

        payload.addField(
                "destinationAccountId",
                "account-2"
        );

        payload.addField(
                "amount",
                0
        );

        scenario.setRequestPayload(
                payload
        );

        List<TestScenario> result =
                filter.filter(
                        endpoint,
                        List.of(scenario)
                );

        assertEquals(
                0,
                result.size()
        );
    }

    @Test
    void shouldKeepSemanticCrossFieldScenario() {

        ApiEndpoint endpoint =
                paymentEndpoint();

        TestScenario scenario =
                new TestScenario(
                        "Reject transfer to same account",
                        "Source and destination must differ",
                        "AI_EXECUTABLE"
                );

        RequestPayload payload =
                new RequestPayload();

        payload.addField(
                "sourceAccountId",
                "account-1"
        );

        payload.addField(
                "destinationAccountId",
                "account-1"
        );

        payload.addField(
                "amount",
                100
        );

        scenario.setRequestPayload(
                payload
        );

        List<TestScenario> result =
                filter.filter(
                        endpoint,
                        List.of(scenario)
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Reject transfer to same account",
                result.get(0).getName()
        );
    }

    @Test
    void shouldRemoveAiExecutableScenarioAboveSchemaMaximum() {

        ApiEndpoint endpoint =
                paymentEndpoint();

        ApiRequestBodyField amountField =
                endpoint.getRequestBody()
                        .getFields()
                        .stream()
                        .filter(
                                field ->
                                        "amount".equals(
                                                field.getName()
                                        )
                        )
                        .findFirst()
                        .orElseThrow();

        amountField.setMaximum(
                new BigDecimal("1000")
        );

        TestScenario scenario =
                new TestScenario(
                        "Reject excessive amount",
                        "Reject amount above maximum",
                        "AI_EXECUTABLE"
                );

        RequestPayload payload =
                new RequestPayload();

        payload.addField(
                "sourceAccountId",
                "account-1"
        );

        payload.addField(
                "destinationAccountId",
                "account-2"
        );

        payload.addField(
                "amount",
                1001
        );

        scenario.setRequestPayload(
                payload
        );

        List<TestScenario> result =
                filter.filter(
                        endpoint,
                        List.of(scenario)
                );

        assertEquals(
                0,
                result.size()
        );
    }

    private ApiEndpoint paymentEndpoint() {

        ApiRequestBodyField sourceAccount =
                new ApiRequestBodyField(
                        "sourceAccountId",
                        "string",
                        true,
                        null
                );

        ApiRequestBodyField destinationAccount =
                new ApiRequestBodyField(
                        "destinationAccountId",
                        "string",
                        true,
                        null
                );

        ApiRequestBodyField amount =
                new ApiRequestBodyField(
                        "amount",
                        "integer",
                        true,
                        null
                );

        amount.setMinimum(
                new BigDecimal("1")
        );

        ApiRequestBody requestBody =
                new ApiRequestBody();

        requestBody.setFields(
                List.of(
                        sourceAccount,
                        destinationAccount,
                        amount
                )
        );

        ApiEndpoint endpoint =
                new ApiEndpoint();

        endpoint.setRequestBody(
                requestBody
        );

        return endpoint;
    }
}