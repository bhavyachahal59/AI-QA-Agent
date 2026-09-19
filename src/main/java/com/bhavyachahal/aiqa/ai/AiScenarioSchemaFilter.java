package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.RequestPayload;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiRequestBodyField;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AiScenarioSchemaFilter {

    public List<TestScenario> filter(
            ApiEndpoint endpoint,
            List<TestScenario> scenarios) {

        if (scenarios == null
                || scenarios.isEmpty()) {

            return List.of();
        }

        if (endpoint == null
                || endpoint.getRequestBody() == null
                || endpoint.getRequestBody().getFields() == null) {

            return scenarios;
        }

        List<TestScenario> filteredScenarios =
                new ArrayList<>();

        for (TestScenario scenario : scenarios) {

            if (isSchemaDerivedNumericViolation(
                    endpoint,
                    scenario)) {

                continue;
            }

            filteredScenarios.add(scenario);
        }

        return filteredScenarios;
    }

    private boolean isSchemaDerivedNumericViolation(
            ApiEndpoint endpoint,
            TestScenario scenario) {

        if (scenario == null
                || !"AI_EXECUTABLE".equals(
                scenario.getType())) {

            return false;
        }

        RequestPayload requestPayload =
                scenario.getRequestPayload();

        if (requestPayload == null
                || requestPayload.getFields() == null
                || requestPayload.getFields().isEmpty()) {

            return false;
        }

        Map<String, Object> payloadFields =
                requestPayload.getFields();

        for (ApiRequestBodyField field :
                endpoint.getRequestBody().getFields()) {

            Object value =
                    payloadFields.get(
                            field.getName()
                    );

            if (!(value instanceof Number number)) {
                continue;
            }

            BigDecimal numericValue =
                    new BigDecimal(
                            number.toString()
                    );

            if (field.getMinimum() != null
                    && numericValue.compareTo(
                    field.getMinimum()
            ) < 0) {

                return true;
            }

            if (field.getMaximum() != null
                    && numericValue.compareTo(
                    field.getMaximum()
            ) > 0) {

                return true;
            }
        }

        return false;
    }
}