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
public class SpeedLimitRule implements AlertRule {

    private static final double MAX_ALLOWED_SPEED = 120.0;

    @Override
    public Optional<Alert> evaluate(TelemetryReading reading) {
        if (reading.speed() <= MAX_ALLOWED_SPEED) {
            return Optional.empty();
        }

        return Optional.of(new Alert(
                UUID.randomUUID(),
                reading.vehicleId(),
                reading.id(),
                AlertType.SPEED_LIMIT_EXCEEDED,
                AlertSeverity.WARNING,
                "Excesso de velocidade acima de 120 km/h",
                Instant.now()
        ));
    }
}
