package com.telemetrypulse.interfaces.rest;

import com.telemetrypulse.infrastructure.persistence.VehicleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public List<TelemetryEventResponse.VehicleStatusResponse> findActive() {
        return vehicleRepository.findActive().stream()
                .map(TelemetryEventResponse.VehicleStatusResponse::from)
                .toList();
    }
}
