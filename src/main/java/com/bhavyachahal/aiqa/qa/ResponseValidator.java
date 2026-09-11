package com.bhavyachahal.aiqa.qa;

import org.springframework.stereotype.Service;

@Service
public class ResponseValidator {

    public boolean validateStatusCode(
            int actualStatusCode,
            String expectedStatusCode) {

        if (expectedStatusCode == null) {
            return false;
        }

        return String.valueOf(actualStatusCode)
                .equals(expectedStatusCode);
    }
}