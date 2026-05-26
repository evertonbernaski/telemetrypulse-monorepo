package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.application.TelemetryIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryIngestionService telemetryIngestionService;

    public TelemetryController(TelemetryIngestionService telemetryIngestionService) {
        this.telemetryIngestionService = telemetryIngestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TelemetryEventResponse ingest(@Valid @RequestBody TelemetryIngestRequest request) {
        return telemetryIngestionService.ingest(request);
    }
}
