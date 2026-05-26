package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.application.TelemetryIngestionService;
import com.telemetrypulse.infrastructure.persistence.TelemetryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@Validated
public class TelemetryController {

    private final TelemetryIngestionService telemetryIngestionService;
    private final TelemetryRepository telemetryRepository;

    public TelemetryController(
            TelemetryIngestionService telemetryIngestionService,
            TelemetryRepository telemetryRepository
    ) {
        this.telemetryIngestionService = telemetryIngestionService;
        this.telemetryRepository = telemetryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TelemetryEventResponse ingest(@Valid @RequestBody TelemetryIngestRequest request) {
        return telemetryIngestionService.ingest(request);
    }

    @GetMapping
    public List<TelemetryHistoryResponse> findHistory(
            @RequestParam @NotBlank String vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "100") @Min(1) @Max(500) int limit
    ) {
        return telemetryRepository.findHistory(vehicleId, from, to, limit).stream()
                .map(TelemetryHistoryResponse::from)
                .toList();
    }
}
