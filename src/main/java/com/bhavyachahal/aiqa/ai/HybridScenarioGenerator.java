package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HybridScenarioGenerator {

    private final TestScenarioGenerator deterministicGenerator;
    private final AiScenarioGenerator aiScenarioGenerator;
    private final ScenarioMerger scenarioMerger;

    public HybridScenarioGenerator(
            TestScenarioGenerator deterministicGenerator,
            AiScenarioGenerator aiScenarioGenerator,
            ScenarioMerger scenarioMerger) {

        this.deterministicGenerator =
                deterministicGenerator;

        this.aiScenarioGenerator =
                aiScenarioGenerator;

        this.scenarioMerger =
                scenarioMerger;
    }

    public List<TestScenario> generateScenarios(
            ApiEndpoint endpoint) {

        List<TestScenario> deterministicScenarios =
                deterministicGenerator.generate(
                        endpoint
                );

        List<TestScenario> aiScenarios =
                aiScenarioGenerator.generateScenarios(
                        endpoint
                );

        return scenarioMerger.merge(
                deterministicScenarios,
                aiScenarios
        );
    }
}