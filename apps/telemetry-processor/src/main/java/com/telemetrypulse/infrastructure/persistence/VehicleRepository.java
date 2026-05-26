package com.telemetrypulse.infrastructure.persistence;

import com.telemetrypulse.domain.VehicleState;
import com.telemetrypulse.domain.VehicleStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class VehicleRepository {

    private static final RowMapper<VehicleStatus> ROW_MAPPER = (rs, rowNum) -> new VehicleStatus(
            rs.getString("id"),
            rs.getDouble("battery_level"),
            rs.getDouble("speed"),
            rs.getDouble("motor_temperature"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            VehicleState.valueOf(rs.getString("status")),
            rs.getTimestamp("last_telemetry_at").toInstant()
    );

    private final JdbcClient jdbcClient;

    public VehicleRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void upsert(VehicleStatus status) {
        jdbcClient.sql("""
                INSERT INTO vehicles (
                    id, battery_level, speed, motor_temperature, latitude,
                    longitude, status, last_telemetry_at, updated_at
                )
                VALUES (
                    :id, :batteryLevel, :speed, :motorTemperature, :latitude,
                    :longitude, :status, :lastTelemetryAt, NOW()
                )
                ON CONFLICT (id) DO UPDATE SET
                    battery_level = EXCLUDED.battery_level,
                    speed = EXCLUDED.speed,
                    motor_temperature = EXCLUDED.motor_temperature,
                    latitude = EXCLUDED.latitude,
                    longitude = EXCLUDED.longitude,
                    status = EXCLUDED.status,
                    last_telemetry_at = EXCLUDED.last_telemetry_at,
                    updated_at = NOW()
                """)
                .param("id", status.vehicleId())
                .param("batteryLevel", status.batteryLevel())
                .param("speed", status.speed())
                .param("motorTemperature", status.motorTemperature())
                .param("latitude", status.latitude())
                .param("longitude", status.longitude())
                .param("status", status.status().name())
                .param("lastTelemetryAt", Timestamp.from(status.lastTelemetryAt()))
                .update();
    }

    public List<VehicleStatus> findActive() {
        return jdbcClient.sql("""
                SELECT id, battery_level, speed, motor_temperature, latitude,
                       longitude, status, last_telemetry_at
                  FROM vehicles
                 ORDER BY last_telemetry_at DESC
                """)
                .query(ROW_MAPPER)
                .list();
    }
}
