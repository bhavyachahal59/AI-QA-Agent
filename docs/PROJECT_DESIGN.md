# AI QA Agent for REST APIs

## 1. Project Goal

Build an AI-powered QA agent that accepts a REST API specification, understands the available endpoints and contracts, generates intelligent test scenarios, executes those tests against a target API, analyzes the responses, and produces a structured QA report.

The project should demonstrate strong software engineering fundamentals together with practical AI/LLM integration.

---

## 2. MVP Workflow

```text
OpenAPI Specification
        |
        v
   API Spec Parser
        |
        v
   AI Test Generator
        |
        v
 Executable Test Cases
        |
        v
   Test Executor
        |
        v
 Response / Result Analyzer
        |
        v
    QA Report

3. MVP Functional Requirements
FR-01: API Specification Input

The system must accept an OpenAPI 3.x specification.

Initial supported formats:

YAML
JSON

The specification should describe:

endpoints
HTTP methods
parameters
request bodies
required fields
response schemas
authentication requirements
FR-02: API Specification Parsing

The system must parse the OpenAPI specification into an internal representation.

The representation should allow the application to understand:

API endpoints
HTTP methods
parameters
request schemas
response schemas
authentication requirements
FR-03: AI Test Scenario Generation

The AI engine should analyze the API definition and generate test scenarios.

The initial test categories should include:

Happy-path tests
Negative tests
Validation tests
Boundary-value tests
Authentication/authorization tests
Invalid parameter tests
Invalid request-body tests
Response-schema tests
HTTP status-code tests
FR-04: Executable Test Cases

Each generated scenario must be converted into a structured test case containing:

test case ID
endpoint
HTTP method
request parameters
request body
expected status
expected response characteristics
test category
test rationale
FR-05: Test Execution

The system must execute generated test cases against a target REST API.

For each execution it should capture:

request
response
HTTP status
response body
response headers
execution time
FR-06: Result Analysis

The system must determine whether each test passed or failed.

The analysis should distinguish between:

Expected success
Expected failure
Unexpected API behavior
Invalid test configuration
Environment/infrastructure failure
FR-07: AI Failure Analysis

For failed tests, the AI engine should provide:

failure explanation
probable root cause
severity
supporting evidence
suggested investigation/fix

The AI must clearly distinguish facts observed from the response from hypotheses.

FR-08: QA Report

The system must produce a structured test-run report containing:

total tests
passed tests
failed tests
skipped tests
pass percentage
failures grouped by severity
individual test results
AI-generated failure analysis
4. Non-Functional Requirements
NFR-01: Maintainability

The application should use clear separation of responsibilities and modular components.

NFR-02: Testability

Core business logic must be independently unit-testable.

NFR-03: Observability

The application should provide useful logs and health information.

NFR-04: Configuration

Environment-specific configuration must not be hard-coded.

NFR-05: Security

API credentials, API keys, and LLM credentials must never be committed to Git.

NFR-06: Reliability

Failures in one test execution should not terminate the complete test run.

NFR-07: Extensibility

The architecture should allow additional:

LLM providers
API specification formats
test-generation strategies
report formats
authentication mechanisms

to be added later.

5. Initial Technology Stack
Layer	Technology
Language	Java 21
Framework	Spring Boot
Build	Maven
API	Spring Web
API Specification	OpenAPI 3.x
API Execution	REST client / REST Assured
Database	PostgreSQL
Persistence	Spring Data JPA
AI	LLM API
Containers	Docker
Testing	JUnit
API Exploration	Postman
Version Control	Git + GitHub
6. High-Level Architecture
                    Client
                      |
                      v
              REST API Layer
                      |
                      v
              Application Layer
                      |
        +-------------+-------------+
        |             |             |
        v             v             v
   Spec Parser   Test Generator   Test Runner
                      |             |
                      v             v
                  LLM Engine    Target API
                      |
                      v
                 AI Analyzer
                      |
                      v
                 Report Service
                      |
                      v
                 PostgreSQL
7. Initial Module Boundaries
api

Responsible for HTTP endpoints exposed by the application.

application

Responsible for orchestrating use cases.

domain

Contains core business models and rules.

specification

Responsible for OpenAPI parsing and normalization.

testgeneration

Responsible for generating test scenarios and executable test cases.

execution

Responsible for executing HTTP requests and capturing results.

analysis

Responsible for evaluating test results and performing AI-assisted failure analysis.

reporting

Responsible for generating QA reports.

infrastructure

Contains external integrations such as:

PostgreSQL
LLM provider
HTTP clients
configuration
8. Core Domain Concepts

Initial domain objects:

ApiSpecification
ApiEndpoint
TestScenario
TestCase
TestExecution
TestResult
FailureAnalysis
QaReport

These models will evolve as implementation progresses.

9. AI Responsibilities

The AI must not directly control arbitrary system execution.

The AI should primarily be responsible for:

Understanding API contracts
Generating test scenarios
Generating test data
Explaining unexpected responses
Classifying failures
Suggesting probable root causes

Actual HTTP execution must remain under deterministic application control.

10. AI Safety / Reliability Principles

The system should treat LLM output as untrusted structured data.

Therefore:

Validate AI-generated test cases before execution.
Never execute arbitrary code generated by the LLM.
Restrict execution to allowed HTTP operations.
Validate URLs and target environments.
Keep credentials outside prompts where possible.
Store API secrets in environment variables or secure configuration.
Record evidence used for AI-generated conclusions.
11. MVP Scope

The first working version should support:

Upload OpenAPI specification.
Parse API endpoints.
Generate test scenarios using an LLM.
Validate generated scenarios.
Execute tests against a target REST API.
Capture request/response information.
Determine pass/fail.
Analyze failures using the LLM.
Store test-run results.
Produce a QA report.
12. Out of Scope for MVP

The first version will not initially include:

Web frontend
CI/CD integration
Kubernetes deployment
Multi-user authentication
Complex distributed execution
Browser/UI testing
Arbitrary code execution
Autonomous production API testing

These can be considered future enhancements.

13. Future Enhancements

Potential Phase 2 features:

Web dashboard
Postman collection import
Scheduled test runs
CI/CD integration
GitHub Actions integration
Multiple LLM providers
Historical test analytics
Regression detection
Intelligent test prioritization
Contract drift detection
API security testing
Human approval workflow
Parallel test execution
14. Engineering Goal

The project should demonstrate:

Object-oriented design
SOLID principles
REST API design
Spring Boot
Database design
Automated testing
Integration testing
Docker
Git/GitHub
AI/LLM integration
Error handling
Observability
Clean architecture
Production-oriented engineering practices