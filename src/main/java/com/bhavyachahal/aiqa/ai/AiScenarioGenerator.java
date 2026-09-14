package com.bhavyachahal.aiqa.ai;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;

import java.util.List;

public interface AiScenarioGenerator {

    List<TestScenario> generateScenarios(
            ApiEndpoint endpoint
    );
}