package com.bhavyachahal.aiqa.controller;

import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final TestScenarioGenerator scenarioGenerator;

    public QaController(
            TestScenarioGenerator scenarioGenerator) {

        this.scenarioGenerator =
                scenarioGenerator;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {

        return ResponseEntity.ok(
                "AI QA Agent is running"
        );
    }

    @PostMapping("/scenarios")
    public ResponseEntity<List<TestScenario>> generateScenarios(
            @RequestBody ApiEndpoint endpoint) {

        List<TestScenario> scenarios =
                scenarioGenerator.generate(
                        endpoint
                );

        return ResponseEntity.ok(
                scenarios
        );
    }
}