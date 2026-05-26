package com.telemetrypulse.domain;

import java.time.Instant;
import java.util.UUID;

public record TelemetryReading(
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
}
