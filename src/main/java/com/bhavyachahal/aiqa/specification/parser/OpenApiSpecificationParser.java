package com.bhavyachahal.aiqa.specification.parser;

import com.bhavyachahal.aiqa.specification.model.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;

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
        if (operation.getRequestBody() != null) {
            endpoint.setRequestBody(
                    toApiRequestBody(operation.getRequestBody())
            );
        }

        if (operation.getResponses() != null) {
            List<ApiResponse> responses =
                    operation.getResponses()
                            .entrySet()
                            .stream()
                            .map(entry ->
                                    toApiResponse(
                                            entry.getKey(),
                                            entry.getValue()
                                    )
                            )
                            .toList();

            endpoint.setResponses(responses);
        }

        endpoints.add(endpoint);
    }

    private ApiResponse toApiResponse(
            String statusCode,
            io.swagger.v3.oas.models.responses.ApiResponse response) {

        String contentType = null;
        String schemaType = null;
        String schemaName = null;

        Content content = response.getContent();

        if (content != null && !content.isEmpty()) {

            contentType = content.keySet()
                    .iterator()
                    .next();

            Schema<?> schema =
                    content.get(contentType).getSchema();

            if (schema != null) {

                schemaType = schema.getType();

                if (schema.get$ref() != null) {
                    schemaName = schema.get$ref()
                            .substring(
                                    schema.get$ref().lastIndexOf("/") + 1
                            );
                }
            }
        }

        return new ApiResponse(
                statusCode,
                response.getDescription(),
                contentType,
                schemaType,
                schemaName
        );
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

    private ApiRequestBody toApiRequestBody(
            io.swagger.v3.oas.models.parameters.RequestBody requestBody) {

        String contentType = null;
        String schemaType = null;
        String schemaName = null;

        ApiRequestBody apiRequestBody =
                new ApiRequestBody();

        Content content = requestBody.getContent();

        if (content == null || content.isEmpty()) {
            return apiRequestBody;
        }

        contentType = content.keySet()
                .iterator()
                .next();

        MediaType mediaType = content.get(contentType);

        Schema<?> schema = mediaType.getSchema();

        if (schema == null) {
            return apiRequestBody;
        }

        schemaType = schema.getType();

        if (schema.get$ref() != null) {
            schemaName = schema.get$ref()
                    .substring(
                            schema.get$ref().lastIndexOf("/") + 1
                    );
        }

        apiRequestBody.setContentType(contentType);
        apiRequestBody.setSchemaType(schemaType);
        apiRequestBody.setSchemaName(schemaName);

        if (schema.getProperties() != null) {

            List<ApiRequestBodyField> fields =
                    new ArrayList<>();

            schema.getProperties()
                    .forEach((name, propertySchemaObject) -> {

                        Schema<?> propertySchema =
                                (Schema<?>) propertySchemaObject;

                        boolean required =
                                schema.getRequired() != null
                                        && schema.getRequired()
                                        .contains(name);

                        fields.add(
                                new ApiRequestBodyField(
                                        name,
                                        propertySchema.getType(),
                                        required,
                                        propertySchema.getFormat()
                                )
                        );
                    });

            apiRequestBody.setFields(fields);
        }

        return apiRequestBody;
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