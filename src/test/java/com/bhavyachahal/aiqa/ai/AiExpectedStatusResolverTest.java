package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AiExpectedStatusResolverTest {

    private final AiExpectedStatusResolver resolver =
            new AiExpectedStatusResolver();

    @Test
    void shouldResolveUniqueSuccessStatus() {

        ApiEndpoint endpoint =
                endpointWithResponses(
                        "201",
                        "400"
                );

        String result =
                resolver.resolve(
                        endpoint,
                        "ACCEPT"
                );

        assertEquals(
                "201",
                result
        );
    }

    @Test
    void shouldResolveUniqueClientErrorStatus() {

        ApiEndpoint endpoint =
                endpointWithResponses(
                        "201",
                        "400"
                );

        String result =
                resolver.resolve(
                        endpoint,
                        "REJECT"
                );

        assertEquals(
                "400",
                result
        );
    }

    @Test
    void shouldNotGuessBetweenMultipleClientErrorStatuses() {

        ApiEndpoint endpoint =
                endpointWithResponses(
                        "201",
                        "400",
                        "409"
                );

        String result =
                resolver.resolve(
                        endpoint,
                        "REJECT"
                );

        assertNull(result);
    }

    @Test
    void shouldReturnNullForUnknownOutcome() {

        ApiEndpoint endpoint =
                endpointWithResponses(
                        "201",
                        "400"
                );

        String result =
                resolver.resolve(
                        endpoint,
                        "UNKNOWN"
                );

        assertNull(result);
    }

    private ApiEndpoint endpointWithResponses(
            String... statusCodes) {

        ApiEndpoint endpoint =
                new ApiEndpoint();

        List<ApiResponse> responses =
                java.util.Arrays.stream(statusCodes)
                        .map(statusCode -> {
                            ApiResponse response =
                                    new ApiResponse();

                            response.setStatusCode(
                                    statusCode
                            );

                            return response;
                        })
                        .toList();

        endpoint.setResponses(responses);

        return endpoint;
    }
}