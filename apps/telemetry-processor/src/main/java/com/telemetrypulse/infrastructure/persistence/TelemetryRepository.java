package com.telemetrypulse.infrastructure.persistence;

import com.telemetrypulse.domain.TelemetryReading;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class TelemetryRepository {

    private final JdbcClient jdbcClient;

    public TelemetryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(TelemetryReading reading) {
        jdbcClient.sql("""
                INSERT INTO telemetry_readings (
                    id, vehicle_id, battery_level, speed, motor_temperature,
                    latitude, longitude, occurred_at, received_at
                )
                VALUES (
                    :id, :vehicleId, :batteryLevel, :speed, :motorTemperature,
                    :latitude, :longitude, :occurredAt, :receivedAt
                )
                """)
                .param("id", reading.id())
                .param("vehicleId", reading.vehicleId())
                .param("batteryLevel", reading.batteryLevel())
                .param("speed", reading.speed())
                .param("motorTemperature", reading.motorTemperature())
                .param("latitude", reading.latitude())
                .param("longitude", reading.longitude())
                .param("occurredAt", Timestamp.from(reading.occurredAt()))
                .param("receivedAt", Timestamp.from(reading.receivedAt()))
                .update();
    }
}
