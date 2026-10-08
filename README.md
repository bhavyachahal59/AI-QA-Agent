# AI QA Agent for REST APIs

An intelligent REST API testing platform built with **Java, Spring Boot, OpenAPI, and LLM integration** that automatically generates, executes, and evaluates API test scenarios.

The system combines deterministic OpenAPI-based validation with AI-generated semantic test scenarios to identify issues that cannot always be inferred from an API schema alone.

---

## Why This Project?

OpenAPI specifications are useful for describing an API's structure and validation constraints, but they often do not capture all of the business rules an API is expected to enforce.

For example, an API specification may define:

```text
sourceAccountId: string
destinationAccountId: string
amount: integer, minimum: 1
```

A deterministic testing engine can derive scenarios such as:

- Missing `sourceAccountId`
- Missing `destinationAccountId`
- Invalid `amount` datatype
- `amount` below the documented minimum
- Minimum boundary values

However, a business rule such as:

```text
sourceAccountId must not equal destinationAccountId
```

may not be represented in the OpenAPI schema.

The AI QA Agent combines deterministic schema analysis with LLM-based reasoning to discover these additional semantic scenarios.

---

## Architecture

```text
                    OpenAPI Specification
                            |
                            v
                OpenAPI Specification Parser
                            |
                            v
                       API Endpoint
                            |
              +-------------+-------------+
              |                           |
              v                           v
    Deterministic Generator        AI Scenario Generator
              |                           |
              |                    LLM Semantic Reasoning
              |                           |
              |                    Schema-Derived Filter
              |                           |
              |                 +---------+---------+
              |                 |                   |
              |                 v                   v
              |          AI_EXECUTABLE       AI_RECOMMENDATION
              |                 |
              +---------> Scenario Merger
                                |
                                v
                         API Test Service
                                |
                                v
                         API Test Executor
                                |
                                v
                           REST Endpoint
                                |
                                v
                        Response Validator
                                |
                                v
                           Test Report
                    PASS / FAIL / UNVERIFIED
```

---### Detailed Architecture

See the [System Architecture](docs/ARCHITECTURE.md) for the
component diagram, execution flow, and design decisions.

### Sample Execution Report

See the [End-to-End Execution Report](docs/demo/sample-execution-report.json).

The sample run executed 18 scenarios:
- 15 passed
- 0 failed
- 3 unverified
- 100% verified test pass rate

The demo API maintains in-memory state. Restart the application
before reproducing the sample report because test execution can
create or delete users.

## Key Features

### OpenAPI Parsing

Parses OpenAPI specifications and extracts:

- API paths and HTTP methods
- Path, query, and header parameters
- Request body fields
- Required fields
- Response status codes
- Schema constraints
- Minimum and maximum values
- String length constraints
- Regex patterns
- Field formats

### Deterministic Test Generation

Automatically derives test scenarios directly from the API contract, including:

- Valid request scenarios
- Missing required parameters
- Missing required request fields
- Invalid datatypes
- Minimum and maximum violations
- Boundary values
- Empty strings
- Pattern violations
- Format-based validation scenarios

### AI-Powered Semantic Testing

An optional LLM layer analyzes the API contract for test cases that cannot be derived directly from schema constraints.

Example:

```text
Schema:
sourceAccountId: string
destinationAccountId: string
amount: integer

AI semantic scenario:
Reject transfer when sourceAccountId == destinationAccountId
```

AI scenarios are separated into:

**AI_EXECUTABLE**

Scenarios that can be safely executed using information available from the API contract.

**AI_RECOMMENDATION**

Useful scenarios that require external state or setup, such as:

- Insufficient account balance
- Nonexistent resources
- Existing database state
- Previous API requests
- External system behavior

Recommendations are not automatically executed.

### Hybrid Scenario Generation

Deterministic and AI-generated scenarios are merged into a single pipeline.

The deterministic engine remains the source of truth for schema-derived validation, while AI focuses on semantic and business-rule scenarios.

Duplicate scenarios are removed before execution.

### Real HTTP Execution

Generated scenarios are converted into real HTTP requests supporting:

- Path parameters
- Query parameters
- Header parameters
- JSON request bodies
- Configurable target base URL

### Response Validation

Actual HTTP responses are compared against expected behavior derived from the API specification.

Results are classified as:

```text
PASS
FAIL
UNVERIFIED
```

`UNVERIFIED` is used when a scenario can be executed but the OpenAPI specification does not provide enough information to determine one unambiguous expected HTTP status.

### Explainable Results

Execution results expose metadata including:

- Scenario name
- Scenario source
- Scenario type
- Expected semantic outcome
- Expected HTTP status
- Actual HTTP status
- Verification status
- Response body

This makes it possible to distinguish deterministic results from AI-generated results.

### Graceful AI Fallback

AI functionality is optional.

If the LLM is disabled or unavailable because of a timeout, rate limit, API failure, or missing configuration, AI scenario generation safely returns no AI scenarios and the deterministic testing pipeline continues normally.

This prevents an external AI dependency from taking down the core QA engine.

---

## Example

Given an API such as:

```yaml
POST /payments

sourceAccountId:
  type: string

destinationAccountId:
  type: string

amount:
  type: integer
  minimum: 1
```

The deterministic engine can generate:

```text
POSITIVE   | Valid request
VALIDATION | Missing required field: sourceAccountId
VALIDATION | Missing required field: destinationAccountId
VALIDATION | Missing required field: amount
VALIDATION | Invalid integer: amount
VALIDATION | Below minimum: amount
BOUNDARY   | Minimum boundary: amount
```

The AI layer can additionally identify semantic scenarios such as:

```text
AI_EXECUTABLE
Reject transfer when source and destination accounts are identical

AI_RECOMMENDATION
Reject transfer when the source account has insufficient balance
```

---

## Test Reporting

A report tracks:

```text
Total tests
Verified tests
Passed tests
Failed tests
Unverified tests
Pass rate
```

Example result:

```text
UNVERIFIED — Reject transfer to the same account

Expected status: unknown
Actual status: 400
Source: AI
Scenario type: AI_EXECUTABLE
Expected semantic outcome: REJECT
```

Reports can also be rendered as Markdown.

---

## REST API

### Health Check

```http
GET /api/qa/health
```

### Generate Scenarios for an Endpoint

```http
POST /api/qa/scenarios
```

Generates deterministic scenarios for an `ApiEndpoint`.

### Generate Scenarios from OpenAPI

```http
POST /api/qa/openapi/scenarios
```

Parses an OpenAPI specification and generates hybrid deterministic and AI scenarios.

AI scenarios are included when AI integration is enabled and configured.

### Execute an OpenAPI Test Suite

```http
POST /api/qa/openapi/execute
```

Example request:

```json
{
  "baseUrl": "http://localhost:8080",
  "content": "<OpenAPI specification>",
  "format": "yaml"
}
```

The endpoint:

1. Parses the OpenAPI specification
2. Generates deterministic and optional AI scenarios
3. Executes eligible scenarios
4. Validates HTTP responses
5. Returns the complete test report

---

## Technology Stack

- Java 21
- Spring Boot
- Spring RestClient
- Maven
- OpenAPI / Swagger Parser
- OpenAI Java SDK
- JUnit 5
- Mockito
- MockRestServiceServer
- Git / GitHub

---

## Running the Project

### Prerequisites

Install:

- Java 21+
- Maven

Verify:

```bash
java -version
mvn -version
```

### Clone

```bash
git clone https://github.com/bhavyachahal59/AI-QA-Agent.git
cd AI-QA-Agent
```

### Run Tests

```bash
mvn test
```

The normal test suite does not require OpenAI API access.

Real OpenAI integration tests are opt-in and are skipped during the standard Maven test run.

### Start the Application

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

Verify:

```text
GET http://localhost:8080/api/qa/health
```

---

## Optional AI Configuration

AI integration is disabled by default so normal development and testing cannot accidentally consume API credits.

To use live AI scenario generation, provide an OpenAI API key through an environment variable:

```bash
export OPENAI_API_KEY='your-api-key'
```

Never commit API keys to the repository.

Enable AI in the application configuration:

```yaml
ai:
  enabled: true
```

An OpenAI API account with available API credits is required for live AI requests.

If AI is disabled or unavailable, the application continues using deterministic scenario generation.

---

## Testing

The project includes unit, integration, controller, parser, execution, reporting, and AI pipeline tests.

The standard test suite currently contains **58 tests**, with real OpenAI integration tests configured as opt-in tests.

Run the normal suite:

```bash
mvn test
```

Run the real OpenAI integration tests explicitly:

```bash
RUN_OPENAI_INTEGRATION_TESTS=true mvn test
```

Live integration tests require:

- `OPENAI_API_KEY`
- AI API credits
- Network access

---

## Design Principles

The project follows several key principles:

**Deterministic validation first**

Rules explicitly defined in OpenAPI should be tested deterministically rather than delegated to an LLM.

**AI for semantic reasoning**

The LLM focuses on business and cross-field scenarios that cannot be directly derived from the schema.

**Do not blindly trust AI output**

AI-generated scenarios are parsed, classified, filtered, and resolved against the API contract before execution.

**Do not fabricate verification**

When an expected HTTP response cannot be determined unambiguously, the result is marked `UNVERIFIED` rather than incorrectly reported as passed or failed.

**Graceful degradation**

Failure of the external AI provider does not prevent deterministic API testing.

---

## Current Scope

The project currently focuses on contract-driven REST API QA and semantic test generation.

Potential future extensions include:

- Authentication-aware testing
- Stateful workflow testing
- Persistent execution history
- Additional schema-aware AI filtering
- Configurable retries and timeouts
- HTML test reports
- CI/CD integration

---

## Author

**Bhavya Chahal**

Software Engineer | Java | Spring Boot | Distributed Systems | AI-assisted Engineering