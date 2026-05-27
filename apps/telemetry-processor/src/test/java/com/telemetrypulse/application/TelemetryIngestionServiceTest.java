package com.telemetrypulse.application;

import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.infrastructure.persistence.AlertRepository;
import com.telemetrypulse.infrastructure.persistence.TelemetryRepository;
import com.telemetrypulse.infrastructure.persistence.VehicleRepository;
import com.telemetrypulse.infrastructure.streaming.RealtimeEventPublisher;
import com.telemetrypulse.interfaces.rest.telemetry.TelemetryIngestRequest;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TelemetryIngestionServiceTest {

    @Test
    void persistsTelemetryUpdatesVehicleAndPublishesGeneratedAlerts() {
        TelemetryRepository telemetryRepository = mock(TelemetryRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);
        AlertRepository alertRepository = mock(AlertRepository.class);
        RealtimeEventPublisher publisher = mock(RealtimeEventPublisher.class);
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);

        TelemetryIngestionService service = new TelemetryIngestionService(
                clock,
                List.of(new SpeedLimitRule(), new BatteryCriticalRule()),
                telemetryRepository,
                vehicleRepository,
                alertRepository,
                publisher
        );

        TelemetryIngestRequest request = new TelemetryIngestRequest(
                "EV-001",
                12.0,
                130.0,
                72.5,
                -23.5505,
                -46.6333,
                null
        );

        var response = service.ingest(request);

        assertThat(response.vehicle().vehicleId()).isEqualTo("EV-001");
        assertThat(response.vehicle().status().name()).isEqualTo("ATTENTION");
        assertThat(response.alerts())
                .extracting(alert -> alert.type())
                .containsExactlyInAnyOrder(AlertType.SPEED_LIMIT_EXCEEDED, AlertType.BATTERY_CRITICAL);

        verify(telemetryRepository).save(any());
        verify(vehicleRepository).upsert(any());
        verify(alertRepository).saveAll(any());
        verify(publisher).publishTelemetry(any());
    }
}
