package com.telemetrypulse.application;

import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.domain.TelemetryReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlertRulesTest {

    @Test
    void createsSpeedAlertOnlyWhenSpeedIsAboveLimit() {
        SpeedLimitRule rule = new SpeedLimitRule();

        assertThat(rule.evaluate(reading(120.0, 80.0))).isEmpty();
        assertThat(rule.evaluate(reading(121.0, 80.0)))
                .hasValueSatisfying(alert -> assertThat(alert.type()).isEqualTo(AlertType.SPEED_LIMIT_EXCEEDED));
    }

    @Test
    void createsBatteryAlertOnlyWhenBatteryIsBelowThreshold() {
        BatteryCriticalRule rule = new BatteryCriticalRule();

        assertThat(rule.evaluate(reading(80.0, 15.0))).isEmpty();
        assertThat(rule.evaluate(reading(80.0, 14.9)))
                .hasValueSatisfying(alert -> assertThat(alert.type()).isEqualTo(AlertType.BATTERY_CRITICAL));
    }

    private TelemetryReading reading(double speed, double batteryLevel) {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        return new TelemetryReading(
                UUID.randomUUID(),
                "EV-001",
                batteryLevel,
                speed,
                68.0,
                -23.5505,
                -46.6333,
                now,
                now
        );
    }
}
