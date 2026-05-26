package com.telemetrypulse.interfaces.rest;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public record TelemetryIngestRequest(
        @NotBlank String vehicleId,
        @DecimalMin("0.0") @DecimalMax("100.0") double batteryLevel,
        @PositiveOrZero double speed,
        @NotNull double motorTemperature,
        @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
        @PastOrPresent Instant occurredAt
) {
}
