package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.domain.AlertType;
import com.telemetrypulse.infrastructure.persistence.AlertRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<TelemetryEventResponse.AlertResponse> findRecent(
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) AlertType type
    ) {
        return alertRepository.findRecent(vehicleId, type).stream()
                .map(TelemetryEventResponse.AlertResponse::from)
                .toList();
    }
}
