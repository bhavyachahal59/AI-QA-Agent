package com.bhavyachahal.aiqa.specification.parser;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiSpecification;
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