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
                
                Do not generate any scenario that can already be derived from
                the OpenAPI schema.
                
                This includes:
                - missing required fields
                - invalid primitive data types
                - values below minimum or above maximum
                - values at minimum or maximum boundaries
                - strings shorter than minLength or longer than maxLength
                - regex or pattern violations
                - invalid documented formats
                - empty required values
                
                For example, if a numeric field has minimum: 1, do NOT generate
                zero or negative-value scenarios. Those are schema validation
                tests and are handled by the deterministic test generator.
                
                AI scenarios must add behavior that requires semantic reasoning
                beyond individual OpenAPI field constraints.
                
                Prefer cross-field relationships and business rules that cannot
                be expressed by the individual field schemas.

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
                     "type": "AI_EXECUTABLE",
                     "expectedOutcome": "REJECT",
                     "requestBody": {
                       "fieldName": "value"
                     }
                   }
                ]
                
                Use exactly one of these types:
                
                AI_EXECUTABLE
                - Use only when the scenario can be executed using information
                  available directly from the API contract and request itself.
                - It must not require database setup, account state, previous
                  requests, external services, or verification of side effects
                  outside the HTTP response.
                - Include a complete requestBody containing values for all
                  request body fields needed to execute the scenario.
                - Preserve the data types declared by the API contract.
                  Numbers must be JSON numbers, booleans must be JSON booleans,
                  and strings must be JSON strings.
                - The requestBody must specifically represent the semantic
                  condition described by the scenario.
                - Do not classify a scenario as AI_EXECUTABLE if you cannot
                  construct the required request data from the API contract.
                  
                Expected outcome:
                - For AI_EXECUTABLE scenarios, include expectedOutcome.
                - Use exactly one of these values:
                  ACCEPT
                  REJECT
                - ACCEPT means the request should be accepted by the API.
                - REJECT means the request should be rejected by the API.
                - Do NOT generate or guess HTTP status codes.
                - The deterministic engine will map the semantic outcome to a
                  documented OpenAPI response.  
                
                AI_RECOMMENDATION
                - Use when the scenario requires business state, test setup,
                  previous requests, external systems, persistence checks,
                  balance verification, or other information not available
                  directly from the API contract.
                - Do not include requestBody unless it provides meaningful
                  illustrative information.
                
                Be conservative. If you are uncertain whether a scenario can
                be executed independently, classify it as AI_RECOMMENDATION.
                
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