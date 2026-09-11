package com.bhavyachahal.aiqa.qa;

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