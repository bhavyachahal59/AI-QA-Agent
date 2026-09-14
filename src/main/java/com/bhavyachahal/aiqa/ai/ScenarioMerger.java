package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScenarioMerger {

    public List<TestScenario> merge(
            List<TestScenario> deterministicScenarios,
            List<TestScenario> aiScenarios) {

        Map<String, TestScenario> merged =
                new LinkedHashMap<>();

        for (TestScenario scenario :
                deterministicScenarios) {

            merged.put(
                    createKey(scenario),
                    scenario
            );
        }

        for (TestScenario scenario :
                aiScenarios) {

            merged.putIfAbsent(
                    createKey(scenario),
                    scenario
            );
        }

        return new ArrayList<>(
                merged.values()
        );
    }

    private String createKey(
            TestScenario scenario) {

        return String.join(
                "|",
                safe(scenario.getName()),
                safe(scenario.getType()),
                safe(scenario.getDescription())
        );
    }

    private String safe(
            String value) {

        return value == null
                ? ""
                : value.trim().toLowerCase();
    }
}