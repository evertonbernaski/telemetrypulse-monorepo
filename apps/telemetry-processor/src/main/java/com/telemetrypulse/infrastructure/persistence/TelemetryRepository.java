package com.telemetrypulse.infrastructure.persistence;

import com.telemetrypulse.domain.TelemetryReading;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TelemetryRepository {

    private static final RowMapper<TelemetryReading> ROW_MAPPER = (rs, rowNum) -> new TelemetryReading(
            rs.getObject("id", java.util.UUID.class),
            rs.getString("vehicle_id"),
            rs.getDouble("battery_level"),
            rs.getDouble("speed"),
            rs.getDouble("motor_temperature"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            rs.getTimestamp("occurred_at").toInstant(),
            rs.getTimestamp("received_at").toInstant()
    );

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

    public List<TelemetryReading> findHistory(String vehicleId, Instant from, Instant to, int limit) {
        return jdbcClient.sql("""
                SELECT id, vehicle_id, battery_level, speed, motor_temperature,
                       latitude, longitude, occurred_at, received_at
                  FROM telemetry_readings
                 WHERE vehicle_id = :vehicleId
                   AND occurred_at >= :from
                   AND occurred_at <= :to
                 ORDER BY occurred_at DESC
                 LIMIT :limit
                """)
                .param("vehicleId", vehicleId)
                .param("from", Timestamp.from(from))
                .param("to", Timestamp.from(to))
                .param("limit", limit)
                .query(ROW_MAPPER)
                .list();
    }
}
