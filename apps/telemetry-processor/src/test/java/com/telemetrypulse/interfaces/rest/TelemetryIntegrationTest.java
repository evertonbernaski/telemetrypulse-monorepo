package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.domain.AlertType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelemetryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("telemetrypulse")
            .withUsername("telemetrypulse")
            .withPassword("telemetrypulse");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void ingestsTelemetryGeneratesAlertsAndReturnsHistoricalReadings() {
        Instant occurredAt = Instant.parse("2026-05-26T12:00:00Z");
        TelemetryIngestRequest request = new TelemetryIngestRequest(
                "EV-IT-001",
                10.0,
                135.0,
                82.0,
                -23.5505,
                -46.6333,
                occurredAt
        );

        ResponseEntity<TelemetryEventResponse> ingestResponse = restTemplate.postForEntity(
                url("/api/telemetry"),
                new HttpEntity<>(request),
                TelemetryEventResponse.class
        );

        assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(ingestResponse.getBody()).isNotNull();
        assertThat(ingestResponse.getBody().vehicle().vehicleId()).isEqualTo("EV-IT-001");
        assertThat(ingestResponse.getBody().alerts())
                .extracting(TelemetryEventResponse.AlertResponse::type)
                .containsExactlyInAnyOrder(AlertType.BATTERY_CRITICAL, AlertType.SPEED_LIMIT_EXCEEDED);

        ResponseEntity<TelemetryHistoryResponse[]> historyResponse = restTemplate.getForEntity(
                url("/api/telemetry?vehicleId=EV-IT-001&from=2026-05-26T11:00:00Z&to=2026-05-26T13:00:00Z"),
                TelemetryHistoryResponse[].class
        );

        assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(historyResponse.getBody())
                .isNotNull()
                .singleElement()
                .satisfies(reading -> {
                    assertThat(reading.vehicleId()).isEqualTo("EV-IT-001");
                    assertThat(reading.speed()).isEqualTo(135.0);
                    assertThat(reading.batteryLevel()).isEqualTo(10.0);
                });
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
