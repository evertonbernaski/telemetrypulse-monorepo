package com.telemetrypulse.application;

import com.telemetrypulse.domain.Alert;
import com.telemetrypulse.domain.TelemetryReading;
import com.telemetrypulse.domain.VehicleState;
import com.telemetrypulse.domain.VehicleStatus;
import com.telemetrypulse.infrastructure.persistence.AlertRepository;
import com.telemetrypulse.infrastructure.persistence.TelemetryRepository;
import com.telemetrypulse.infrastructure.persistence.VehicleRepository;
import com.telemetrypulse.infrastructure.streaming.RealtimeEventPublisher;
import com.telemetrypulse.interfaces.rest.TelemetryEventResponse;
import com.telemetrypulse.interfaces.rest.TelemetryIngestRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TelemetryIngestionService {

    private final Clock clock;
    private final List<AlertRule> alertRules;
    private final TelemetryRepository telemetryRepository;
    private final VehicleRepository vehicleRepository;
    private final AlertRepository alertRepository;
    private final RealtimeEventPublisher realtimeEventPublisher;

    public TelemetryIngestionService(
            Clock clock,
            List<AlertRule> alertRules,
            TelemetryRepository telemetryRepository,
            VehicleRepository vehicleRepository,
            AlertRepository alertRepository,
            RealtimeEventPublisher realtimeEventPublisher
    ) {
        this.clock = clock;
        this.alertRules = alertRules;
        this.telemetryRepository = telemetryRepository;
        this.vehicleRepository = vehicleRepository;
        this.alertRepository = alertRepository;
        this.realtimeEventPublisher = realtimeEventPublisher;
    }

    @Transactional
    public TelemetryEventResponse ingest(TelemetryIngestRequest request) {
        Instant receivedAt = Instant.now(clock);
        TelemetryReading reading = new TelemetryReading(
                UUID.randomUUID(),
                request.vehicleId(),
                request.batteryLevel(),
                request.speed(),
                request.motorTemperature(),
                request.latitude(),
                request.longitude(),
                request.occurredAt() == null ? receivedAt : request.occurredAt(),
                receivedAt
        );

        telemetryRepository.save(reading);

        List<Alert> alerts = alertRules.stream()
                .flatMap(rule -> rule.evaluate(reading).stream())
                .toList();

        VehicleState state = alerts.isEmpty() ? VehicleState.ONLINE : VehicleState.ATTENTION;
        VehicleStatus status = new VehicleStatus(
                reading.vehicleId(),
                reading.batteryLevel(),
                reading.speed(),
                reading.motorTemperature(),
                reading.latitude(),
                reading.longitude(),
                state,
                reading.occurredAt()
        );

        vehicleRepository.upsert(status);
        alertRepository.saveAll(alerts);

        TelemetryEventResponse response = TelemetryEventResponse.from(status, alerts);
        realtimeEventPublisher.publishTelemetry(response);
        alerts.forEach(alert -> realtimeEventPublisher.publishAlert(TelemetryEventResponse.AlertResponse.from(alert)));

        return response;
    }
}
