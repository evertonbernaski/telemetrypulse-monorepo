package com.telemetrypulse.domain;

import java.time.Instant;
import java.util.UUID;

public record Alert(
        UUID id,
        String vehicleId,
        UUID telemetryId,
        AlertType type,
        AlertSeverity severity,
        String message,
        Instant createdAt
) {
}
