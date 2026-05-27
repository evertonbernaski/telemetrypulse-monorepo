package com.telemetrypulse.interfaces.rest.telemetry;

import com.telemetrypulse.domain.TelemetryReading;

import java.time.Instant;
import java.util.UUID;

public record TelemetryHistoryResponse(
        UUID id,
        String vehicleId,
        double batteryLevel,
        double speed,
        double motorTemperature,
        double latitude,
        double longitude,
        Instant occurredAt,
        Instant receivedAt
) {
    public static TelemetryHistoryResponse from(TelemetryReading reading) {
        return new TelemetryHistoryResponse(
                reading.id(),
                reading.vehicleId(),
                reading.batteryLevel(),
                reading.speed(),
                reading.motorTemperature(),
                reading.latitude(),
                reading.longitude(),
                reading.occurredAt(),
                reading.receivedAt()
        );
    }
}
