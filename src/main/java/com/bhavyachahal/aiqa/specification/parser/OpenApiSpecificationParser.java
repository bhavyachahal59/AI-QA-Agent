package com.bhavyachahal.aiqa.specification.parser;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiSpecification;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OpenApiSpecificationParser {

    public ApiSpecification parse(String content, String format) {

        SwaggerParseResult result =
                new OpenAPIV3Parser().readContents(content);

        if (result.getOpenAPI() == null) {
            throw new IllegalArgumentException(
                    "Invalid OpenAPI specification: " + result.getMessages()
            );
        }

        OpenAPI openAPI = result.getOpenAPI();

        String name = openAPI.getInfo() != null
                ? openAPI.getInfo().getTitle()
                : "Unknown API";

        String version = openAPI.getInfo() != null
                ? openAPI.getInfo().getVersion()
                : "Unknown";

        ApiSpecification specification = new ApiSpecification(
                UUID.randomUUID(),
                name,
                version,
                content,
                format,
                Instant.now()
        );

        return specification;
    }

    public List<ApiEndpoint> parseEndpoints(
            String content,
            UUID specificationId) {

        SwaggerParseResult result =
                new OpenAPIV3Parser().readContents(content);

        if (result.getOpenAPI() == null) {
            throw new IllegalArgumentException(
                    "Invalid OpenAPI specification: " + result.getMessages()
            );
        }

        OpenAPI openAPI = result.getOpenAPI();

        List<ApiEndpoint> endpoints = new ArrayList<>();

        if (openAPI.getPaths() == null) {
            return endpoints;
        }

        openAPI.getPaths().forEach((path, pathItem) -> {

            if (pathItem.getGet() != null) {
                endpoints.add(createEndpoint(
                        specificationId,
                        path,
                        "GET",
                        pathItem.getGet().getSummary()
                ));
            }

            if (pathItem.getPost() != null) {
                endpoints.add(createEndpoint(
                        specificationId,
                        path,
                        "POST",
                        pathItem.getPost().getSummary()
                ));
            }

            if (pathItem.getPut() != null) {
                endpoints.add(createEndpoint(
                        specificationId,
                        path,
                        "PUT",
                        pathItem.getPut().getSummary()
                ));
            }

            if (pathItem.getDelete() != null) {
                endpoints.add(createEndpoint(
                        specificationId,
                        path,
                        "DELETE",
                        pathItem.getDelete().getSummary()
                ));
            }

            if (pathItem.getPatch() != null) {
                endpoints.add(createEndpoint(
                        specificationId,
                        path,
                        "PATCH",
                        pathItem.getPatch().getSummary()
                ));
            }
        });

        return endpoints;
    }

    private ApiEndpoint createEndpoint(
            UUID specificationId,
            String path,
            String method,
            String summary) {

        return new ApiEndpoint(
                UUID.randomUUID(),
                specificationId,
                path,
                method,
                summary
        );
    }
}