package com.bhavyachahal.aiqa.specification.parser;

import com.bhavyachahal.aiqa.specification.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiSpecificationParserTest {

    private final OpenApiSpecificationParser parser =
            new OpenApiSpecificationParser();

    @Test
    void shouldParseApiSpecification() throws IOException {

        String content = loadSpecification();

        ApiSpecification specification =
                parser.parse(content, "YAML");

        assertNotNull(specification);
        assertEquals("Sample User API", specification.getName());
        assertEquals("1.0.0", specification.getVersion());
        assertEquals("YAML", specification.getFormat());
        assertNotNull(specification.getId());
        assertNotNull(specification.getCreatedAt());
    }

    @Test
    void shouldParseAllApiEndpoints() throws IOException {

        String content = loadSpecification();

        ApiSpecification specification =
                parser.parse(content, "YAML");

        List<ApiEndpoint> endpoints =
                parser.parseEndpoints(content, specification.getId());
        ApiEndpoint userByIdEndpoint = endpoints.stream()
                .filter(endpoint ->
                        endpoint.getMethod().equals("GET")
                                && endpoint.getPath().equals("/users/{id}"))
                .findFirst()
                .orElseThrow();

        assertEquals(1, userByIdEndpoint.getParameters().size());

        ApiParameter idParameter =
                userByIdEndpoint.getParameters().get(0);

        assertEquals("id", idParameter.getName());
        assertEquals("path", idParameter.getLocation());
        assertTrue(idParameter.isRequired());
        assertEquals("integer", idParameter.getType());

        assertEquals(4, endpoints.size());

        assertTrue(endpoints.stream()
                .anyMatch(endpoint ->
                        endpoint.getMethod().equals("GET")
                                && endpoint.getPath().equals("/users")));

        assertTrue(endpoints.stream()
                .anyMatch(endpoint ->
                        endpoint.getMethod().equals("POST")
                                && endpoint.getPath().equals("/users")));

        assertTrue(endpoints.stream()
                .anyMatch(endpoint ->
                        endpoint.getMethod().equals("GET")
                                && endpoint.getPath().equals("/users/{id}")));

        assertTrue(endpoints.stream()
                .anyMatch(endpoint ->
                        endpoint.getMethod().equals("DELETE")
                                && endpoint.getPath().equals("/users/{id}")));

        ApiEndpoint createUserEndpoint = endpoints.stream()
                .filter(endpoint ->
                        endpoint.getMethod().equals("POST")
                                && endpoint.getPath().equals("/users"))
                .findFirst()
                .orElseThrow();

        ApiRequestBody requestBody =
                createUserEndpoint.getRequestBody();

        assertNotNull(requestBody);

        assertEquals(
                "application/json",
                requestBody.getContentType()
        );

        assertEquals(
                "object",
                requestBody.getSchemaType()
        );

        assertFalse(requestBody.getFields().isEmpty());

        assertEquals(
                3,
                requestBody.getFields().size()
        );

        ApiRequestBodyField nameField =
                requestBody.getFields()
                        .stream()
                        .filter(field ->
                                field.getName().equals("name"))
                        .findFirst()
                        .orElseThrow();

        assertEquals("string", nameField.getType());
        assertTrue(nameField.isRequired());

        ApiRequestBodyField emailField =
                requestBody.getFields()
                        .stream()
                        .filter(field ->
                                field.getName().equals("email"))
                        .findFirst()
                        .orElseThrow();

        assertEquals("string", emailField.getType());
        assertTrue(emailField.isRequired());

        ApiRequestBodyField ageField =
                requestBody.getFields()
                        .stream()
                        .filter(field ->
                                field.getName().equals("age"))
                        .findFirst()
                        .orElseThrow();

        assertEquals("integer", ageField.getType());
        assertFalse(ageField.isRequired());

        assertNotNull(createUserEndpoint.getRequestBody());

        assertEquals(
                "application/json",
                createUserEndpoint.getRequestBody().getContentType()
        );

        assertEquals(
                "object",
                createUserEndpoint.getRequestBody().getSchemaType()
        );

        ApiEndpoint getUserEndpoint = endpoints.stream()
                .filter(endpoint ->
                        endpoint.getMethod().equals("GET")
                                && endpoint.getPath().equals("/users/{id}"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, getUserEndpoint.getResponses().size());

        ApiResponse successResponse =
                getUserEndpoint.getResponses()
                        .stream()
                        .filter(response ->
                                response.getStatusCode().equals("200"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "User found",
                successResponse.getDescription()
        );

        assertEquals(
                "application/json",
                successResponse.getContentType()
        );

        assertEquals(
                "object",
                successResponse.getSchemaType()
        );

        ApiResponse notFoundResponse =
                getUserEndpoint.getResponses()
                        .stream()
                        .filter(response ->
                                response.getStatusCode().equals("404"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                "User not found",
                notFoundResponse.getDescription()
        );
    }

    private String loadSpecification() throws IOException {

        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream("sample-api.yaml")) {

            assertNotNull(inputStream, "sample-api.yaml not found");

            return new String(
                    inputStream.readAllBytes()
            );
        }
    }
}