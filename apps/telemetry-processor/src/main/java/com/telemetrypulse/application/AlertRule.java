package com.telemetrypulse.application;

import com.telemetrypulse.domain.Alert;
import com.telemetrypulse.domain.TelemetryReading;

import java.util.Optional;

public interface AlertRule {
    Optional<Alert> evaluate(TelemetryReading reading);
}
