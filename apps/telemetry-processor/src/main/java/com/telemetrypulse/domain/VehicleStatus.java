package com.telemetrypulse.domain;

import java.time.Instant;

public record VehicleStatus(
        String vehicleId,
        double batteryLevel,
        double speed,
        double motorTemperature,
        double latitude,
        double longitude,
        VehicleState status,
        Instant lastTelemetryAt
) {
}
