package com.bhavyachahal.aiqa.controller;

import com.bhavyachahal.aiqa.controller.model.OpenApiScenarioRequest;
import com.bhavyachahal.aiqa.qa.TestScenarioGenerator;
import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiSpecification;
import com.bhavyachahal.aiqa.specification.parser.OpenApiSpecificationParser;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final TestScenarioGenerator scenarioGenerator;
    private final OpenApiSpecificationParser specificationParser;

    public QaController(
            TestScenarioGenerator scenarioGenerator,
            OpenApiSpecificationParser specificationParser) {

        this.scenarioGenerator =
                scenarioGenerator;

        this.specificationParser =
                specificationParser;
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

    @PostMapping("/openapi/scenarios")
    public ResponseEntity<List<TestScenario>> generateScenariosFromOpenApi(
            @RequestBody OpenApiScenarioRequest request) {

        ApiSpecification specification =
                specificationParser.parse(
                        request.getContent(),
                        request.getFormat()
                );

        List<ApiEndpoint> endpoints =
                specificationParser.parseEndpoints(
                        request.getContent(),
                        specification.getId()
                );

        List<TestScenario> scenarios =
                new ArrayList<>();

        for (ApiEndpoint endpoint : endpoints) {

            scenarios.addAll(
                    scenarioGenerator.generate(
                            endpoint
                    )
            );
        }

        return ResponseEntity.ok(
                scenarios
        );
    }
}