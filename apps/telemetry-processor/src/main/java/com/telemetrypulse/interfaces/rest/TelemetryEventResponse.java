package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.domain.Alert;
import com.telemetrypulse.domain.AlertSeverity;
import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.domain.VehicleState;
import com.telemetrypulse.domain.VehicleStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TelemetryEventResponse(
        VehicleStatusResponse vehicle,
        List<AlertResponse> alerts
) {
    public static TelemetryEventResponse from(VehicleStatus status, List<Alert> alerts) {
        return new TelemetryEventResponse(
                VehicleStatusResponse.from(status),
                alerts.stream().map(AlertResponse::from).toList()
        );
    }

    public record VehicleStatusResponse(
            String vehicleId,
            double batteryLevel,
            double speed,
            double motorTemperature,
            double latitude,
            double longitude,
            VehicleState status,
            Instant lastTelemetryAt
    ) {
        public static VehicleStatusResponse from(VehicleStatus status) {
            return new VehicleStatusResponse(
                    status.vehicleId(),
                    status.batteryLevel(),
                    status.speed(),
                    status.motorTemperature(),
                    status.latitude(),
                    status.longitude(),
                    status.status(),
                    status.lastTelemetryAt()
            );
        }
    }

    public record AlertResponse(
            UUID id,
            String vehicleId,
            AlertType type,
            AlertSeverity severity,
            String message,
            Instant createdAt
    ) {
        public static AlertResponse from(Alert alert) {
            return new AlertResponse(
                    alert.id(),
                    alert.vehicleId(),
                    alert.type(),
                    alert.severity(),
                    alert.message(),
                    alert.createdAt()
            );
        }
    }
}
