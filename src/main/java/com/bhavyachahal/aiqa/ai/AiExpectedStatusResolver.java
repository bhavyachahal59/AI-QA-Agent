package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiExpectedStatusResolver {

    public String resolve(
            ApiEndpoint endpoint,
            String expectedOutcome) {

        if (endpoint == null
                || expectedOutcome == null
                || expectedOutcome.isBlank()
                || endpoint.getResponses() == null) {

            return null;
        }

        List<ApiResponse> responses =
                endpoint.getResponses();

        if ("ACCEPT".equalsIgnoreCase(expectedOutcome)) {
            return resolveUniqueStatusInRange(
                    responses,
                    200,
                    299
            );
        }

        if ("REJECT".equalsIgnoreCase(expectedOutcome)) {
            return resolveUniqueStatusInRange(
                    responses,
                    400,
                    499
            );
        }

        return null;
    }

    private String resolveUniqueStatusInRange(
            List<ApiResponse> responses,
            int minimum,
            int maximum) {

        List<String> matchingStatusCodes =
                responses.stream()
                        .map(ApiResponse::getStatusCode)
                        .filter(this::isNumericStatusCode)
                        .filter(statusCode -> {
                            int code =
                                    Integer.parseInt(
                                            statusCode
                                    );

                            return code >= minimum
                                    && code <= maximum;
                        })
                        .distinct()
                        .toList();

        if (matchingStatusCodes.size() != 1) {
            return null;
        }

        return matchingStatusCodes.get(0);
    }

    private boolean isNumericStatusCode(
            String statusCode) {

        if (statusCode == null
                || !statusCode.matches("\\d{3}")) {

            return false;
        }

        return true;
    }
}