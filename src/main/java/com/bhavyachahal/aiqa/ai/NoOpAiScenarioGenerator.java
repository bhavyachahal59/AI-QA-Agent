package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;

public class NoOpAiScenarioGenerator
        implements com.bhavyachahal.aiqa.ai.AiScenarioGenerator {

    @Override
    public List<TestScenario> generateScenarios(
            ApiEndpoint endpoint) {

        return List.of();
    }
}