package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.interfaces.rest.telemetry.TelemetryEventResponse;
import com.telemetrypulse.interfaces.rest.telemetry.TelemetryHistoryResponse;
import com.telemetrypulse.interfaces.rest.telemetry.TelemetryIngestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:telemetrypulse;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.flyway.enabled=true",
                "spring.flyway.locations=classpath:db/test-migration"
        }
)
class TelemetryIntegrationTest {

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
