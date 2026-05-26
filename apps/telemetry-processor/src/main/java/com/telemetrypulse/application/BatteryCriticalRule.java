package com.telemetrypulse.application;

import com.telemetrypulse.domain.Alert;
import com.telemetrypulse.domain.AlertSeverity;
import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.domain.TelemetryReading;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class BatteryCriticalRule implements AlertRule {

    private static final double CRITICAL_BATTERY_THRESHOLD = 15.0;

    @Override
    public Optional<Alert> evaluate(TelemetryReading reading) {
        if (reading.batteryLevel() >= CRITICAL_BATTERY_THRESHOLD) {
            return Optional.empty();
        }

        return Optional.of(new Alert(
                UUID.randomUUID(),
                reading.vehicleId(),
                reading.id(),
                AlertType.BATTERY_CRITICAL,
                AlertSeverity.CRITICAL,
                "Bateria crítica abaixo de 15%",
                Instant.now()
        ));
    }
}
