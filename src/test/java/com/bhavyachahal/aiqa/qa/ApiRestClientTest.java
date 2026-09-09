package com.bhavyachahal.aiqa.qa;

import com.bhavyachahal.aiqa.qa.model.TestScenario;
import com.bhavyachahal.aiqa.specification.model.ApiEndpoint;
import com.bhavyachahal.aiqa.specification.model.ApiParameter;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiRestClientTest {

    @Test
    void shouldExecuteRequestWithQueryAndHeaderParameters()
            throws IOException {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/users",
                exchange -> {

                    assertEquals(
                            "1",
                            exchange.getRequestURI()
                                    .getQuery()
                                    .replace("page=", "")
                    );

                    assertEquals(
                            "test-token",
                            exchange.getRequestHeaders()
                                    .getFirst("X-Test-Token")
                    );

                    String response =
                            "{\"status\":\"ok\"}";

                    exchange.getResponseHeaders()
                            .set(
                                    "Content-Type",
                                    "application/json"
                            );

                    exchange.sendResponseHeaders(
                            200,
                            response.getBytes().length
                    );

                    try (OutputStream outputStream =
                                 exchange.getResponseBody()) {

                        outputStream.write(
                                response.getBytes()
                        );
                    }
                }
        );

        server.start();

        try {

            int port =
                    server.getAddress().getPort();

            RestClient springRestClient =
                    RestClient.builder()
                            .baseUrl(
                                    "http://localhost:" + port
                            )
                            .build();

            ApiRestClient client =
                    new ApiRestClient(
                            springRestClient
                    );

            ApiEndpoint endpoint =
                    new ApiEndpoint();

            endpoint.setPath("/users");
            endpoint.setMethod("GET");

            endpoint.setParameters(
                    java.util.List.of(
                            new ApiParameter(
                                    "page",
                                    "query",
                                    true,
                                    "integer"
                            ),
                            new ApiParameter(
                                    "X-Test-Token",
                                    "header",
                                    true,
                                    "string"
                            )
                    )
            );

            TestScenario scenario =
                    new TestScenario(
                            "Valid request",
                            "Execute a valid request",
                            "POSITIVE"
                    );

            scenario.addParameterValue(
                    "page",
                    1
            );

            scenario.addParameterValue(
                    "X-Test-Token",
                    "test-token"
            );

            ApiRestClient.RestClientResponse response =
                    client.execute(
                            endpoint,
                            scenario
                    );

            assertEquals(
                    200,
                    response.statusCode()
            );

            assertEquals(
                    "{\"status\":\"ok\"}",
                    response.body()
            );

        } finally {

            server.stop(0);
        }
    }
}