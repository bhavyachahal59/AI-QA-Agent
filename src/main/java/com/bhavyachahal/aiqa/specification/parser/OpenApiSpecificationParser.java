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

import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;

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

            addEndpoint(
                    endpoints,
                    specificationId,
                    path,
                    "GET",
                    pathItem.getGet()
            );

            addEndpoint(
                    endpoints,
                    specificationId,
                    path,
                    "POST",
                    pathItem.getPost()
            );

            addEndpoint(
                    endpoints,
                    specificationId,
                    path,
                    "PUT",
                    pathItem.getPut()
            );

            addEndpoint(
                    endpoints,
                    specificationId,
                    path,
                    "DELETE",
                    pathItem.getDelete()
            );

            addEndpoint(
                    endpoints,
                    specificationId,
                    path,
                    "PATCH",
                    pathItem.getPatch()
            );
        });

        return endpoints;
    }

    private void addEndpoint(
            List<ApiEndpoint> endpoints,
            UUID specificationId,
            String path,
            String method,
            Operation operation) {

        if (operation == null) {
            return;
        }

        ApiEndpoint endpoint = createEndpoint(
                specificationId,
                path,
                method,
                operation.getSummary()
        );

        if (operation.getParameters() != null) {

            List<ApiParameter> parameters =
                    operation.getParameters()
                            .stream()
                            .map(this::toApiParameter)
                            .toList();

            endpoint.setParameters(parameters);
        }

        endpoints.add(endpoint);
    }

    private ApiParameter toApiParameter(Parameter parameter) {

        String type = "unknown";

        if (parameter.getSchema() != null) {
            Schema<?> schema = parameter.getSchema();

            if (schema.getType() != null) {
                type = schema.getType();
            }
        }

        return new ApiParameter(
                parameter.getName(),
                parameter.getIn(),
                Boolean.TRUE.equals(parameter.getRequired()),
                type
        );
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