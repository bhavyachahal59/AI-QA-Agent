package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.specification.model.ApiResponse;

import java.util.List;

public class ResponseValidator {

    public ApiResponse findExpectedResponse(
            List<ApiResponse> expectedResponses) {

        if (expectedResponses == null
                || expectedResponses.isEmpty()) {

            return null;
        }

        return expectedResponses.stream()
                .filter(response ->
                        response.getStatusCode() != null
                                && response.getStatusCode().matches("\\d{3}"))
                .findFirst()
                .orElse(null);
    }

    public boolean validateStatusCode(
            int actualStatusCode,
            ApiResponse expectedResponse) {

        if (expectedResponse == null
                || expectedResponse.getStatusCode() == null) {

            return false;
        }

        return String.valueOf(actualStatusCode)
                .equals(expectedResponse.getStatusCode());
    }
}