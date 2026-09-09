package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiRestClient {

    private final org.springframework.web.client.RestClient client;

    public ApiRestClient() {
        this.client =
                org.springframework.web.client.RestClient.create();
    }

    public ApiRestClient(String baseUrl) {
        this.client =
                org.springframework.web.client.RestClient
                        .builder()
                        .baseUrl(baseUrl)
                        .build();
    }

    public ApiRestClient(
            org.springframework.web.client.RestClient client) {

        this.client = client;
    }

    public RestClientResponse execute(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        String path =
                buildPath(
                        endpoint,
                        scenario
                );

        HttpMethod method =
                HttpMethod.valueOf(
                        endpoint.getMethod().toUpperCase()
                );

        org.springframework.web.client.RestClient.RequestBodySpec request =
                client.method(method)
                        .uri(
                                buildUri(
                                        endpoint,
                                        scenario,
                                        path
                                )
                        );

        addHeaders(
                request,
                endpoint,
                scenario
        );

        RequestPayload payload =
                scenario.getRequestPayload();

        if (payload != null) {

            request.body(
                    payload.getFields()
            );
        }

        ResponseEntity<String> response =
                request
                        .retrieve()
                        .toEntity(String.class);

        return new RestClientResponse(
                response.getStatusCode().value(),
                response.getBody()
        );
    }

    private String buildPath(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        String path =
                endpoint.getPath();

        if (endpoint.getParameters() == null) {
            return path;
        }

        for (ApiParameter parameter :
                endpoint.getParameters()) {

            if (!"path".equalsIgnoreCase(
                    parameter.getLocation())) {

                continue;
            }

            Object value =
                    scenario.getParameterValues()
                            .get(parameter.getName());

            if (value == null) {
                continue;
            }

            path =
                    path.replace(
                            "{" + parameter.getName() + "}",
                            encode(value.toString())
                    );
        }

        return path;
    }

    private String buildUri(
            ApiEndpoint endpoint,
            TestScenario scenario,
            String path) {

        StringBuilder uri =
                new StringBuilder(path);

        if (endpoint.getParameters() == null) {
            return uri.toString();
        }

        boolean hasQueryParameter = false;

        for (ApiParameter parameter :
                endpoint.getParameters()) {

            if (!"query".equalsIgnoreCase(
                    parameter.getLocation())) {

                continue;
            }

            Object value =
                    scenario.getParameterValues()
                            .get(parameter.getName());

            if (value == null) {
                continue;
            }

            uri.append(
                    hasQueryParameter
                            ? "&"
                            : "?"
            );

            uri.append(
                    encode(parameter.getName())
            );

            uri.append("=");

            uri.append(
                    encode(value.toString())
            );

            hasQueryParameter = true;
        }

        return uri.toString();
    }

    private void addHeaders(
            org.springframework.web.client.RestClient.RequestHeadersSpec<?> request,
            ApiEndpoint endpoint,
            TestScenario scenario) {

        if (endpoint.getParameters() == null) {
            return;
        }

        Map<String, Object> parameterValues =
                scenario.getParameterValues();

        for (ApiParameter parameter :
                endpoint.getParameters()) {

            if (!"header".equalsIgnoreCase(
                    parameter.getLocation())) {

                continue;
            }

            Object value =
                    parameterValues.get(
                            parameter.getName()
                    );

            if (value != null) {

                request.header(
                        parameter.getName(),
                        value.toString()
                );
            }
        }
    }

    private String encode(String value) {

        return URLEncoder
                .encode(
                        value,
                        StandardCharsets.UTF_8
                );
    }

    public record RestClientResponse(
            int statusCode,
            String body) {
    }
}