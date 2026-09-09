package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseValidatorTest {

    @Test
    void shouldPassWhenStatusCodeMatchesExpectedResponse() {

        ApiResponse expectedResponse =
                new ApiResponse(
                        "201",
                        "User created",
                        "application/json",
                        "object",
                        "User"
                );

        ResponseValidator validator =
                new ResponseValidator();

        assertTrue(
                validator.validateStatusCode(
                        201,
                        expectedResponse
                )
        );
    }

    @Test
    void shouldFailWhenStatusCodeDoesNotMatchExpectedResponse() {

        ApiResponse expectedResponse =
                new ApiResponse(
                        "201",
                        "User created",
                        "application/json",
                        "object",
                        "User"
                );

        ResponseValidator validator =
                new ResponseValidator();

        assertFalse(
                validator.validateStatusCode(
                        400,
                        expectedResponse
                )
        );
    }
}