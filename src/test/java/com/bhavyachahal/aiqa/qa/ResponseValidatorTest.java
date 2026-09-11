package com.bhavyachahal.aiqa.qa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseValidatorTest {

    @Test
    void shouldPassWhenStatusCodeMatchesExpectedStatusCode() {

        ResponseValidator validator =
                new ResponseValidator();

        assertTrue(
                validator.validateStatusCode(
                        201,
                        "201"
                )
        );
    }

    @Test
    void shouldFailWhenStatusCodeDoesNotMatchExpectedStatusCode() {

        ResponseValidator validator =
                new ResponseValidator();

        assertFalse(
                validator.validateStatusCode(
                        400,
                        "201"
                )
        );
    }

    @Test
    void shouldFailWhenExpectedStatusCodeIsMissing() {

        ResponseValidator validator =
                new ResponseValidator();

        assertFalse(
                validator.validateStatusCode(
                        200,
                        null
                )
        );
    }
}