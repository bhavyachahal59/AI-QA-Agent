package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import org.springframework.stereotype.Component;

@Component
public class AiScenarioPromptBuilder {

    public String build(ApiEndpoint endpoint) {

        StringBuilder prompt =
                new StringBuilder();

        prompt.append("""
                You are an expert API QA engineer.

                Generate additional semantic and business-rule test scenarios
                for the API endpoint below.

                Do not generate basic schema-validation scenarios such as
                missing required fields, invalid primitive types, minimum or
                maximum boundaries, or empty strings. Those are generated
                separately by a deterministic test engine.

                Focus on scenarios that require semantic reasoning or business
                knowledge that cannot be derived directly from the OpenAPI
                schema.

                Return only JSON.

                Endpoint:
                """);

        prompt.append("Method: ")
                .append(endpoint.getMethod())
                .append("\n");

        prompt.append("Path: ")
                .append(endpoint.getPath())
                .append("\n");

        prompt.append("Summary: ")
                .append(endpoint.getSummary())
                .append("\n");

        appendParameters(
                prompt,
                endpoint
        );

        appendRequestBody(
                prompt,
                endpoint
        );

        prompt.append("""
                
                Return a JSON array using exactly this structure:

                [
                  {
                    "name": "Scenario name",
                    "description": "What business behavior should be tested",
                    "type": "AI_SEMANTIC"
                  }
                ]

                Generate at most 5 high-value scenarios.
                Do not include explanations outside the JSON array.
                """);

        return prompt.toString();
    }

    private void appendParameters(
            StringBuilder prompt,
            ApiEndpoint endpoint) {

        if (endpoint.getParameters() == null
                || endpoint.getParameters().isEmpty()) {

            return;
        }

        prompt.append("\nParameters:\n");

        for (ApiParameter parameter :
                endpoint.getParameters()) {

            prompt.append("- ")
                    .append(parameter.getName())
                    .append(" | location=")
                    .append(parameter.getLocation())
                    .append(" | type=")
                    .append(parameter.getType())
                    .append(" | required=")
                    .append(parameter.isRequired())
                    .append("\n");
        }
    }

    private void appendRequestBody(
            StringBuilder prompt,
            ApiEndpoint endpoint) {

        if (endpoint.getRequestBody() == null
                || endpoint.getRequestBody()
                .getFields() == null
                || endpoint.getRequestBody()
                .getFields()
                .isEmpty()) {

            return;
        }

        prompt.append("\nRequest body fields:\n");

        for (ApiRequestBodyField field :
                endpoint.getRequestBody()
                        .getFields()) {

            prompt.append("- ")
                    .append(field.getName())
                    .append(" | type=")
                    .append(field.getType())
                    .append(" | format=")
                    .append(field.getFormat())
                    .append(" | required=")
                    .append(field.isRequired())
                    .append("\n");
        }
    }
}