package com.telemetrypulse.infrastructure.persistence;

import com.telemetrypulse.domain.Alert;
import com.telemetrypulse.domain.AlertSeverity;
import com.telemetrypulse.domain.AlertType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AlertRepository {

    private static final RowMapper<Alert> ROW_MAPPER = (rs, rowNum) -> new Alert(
            rs.getObject("id", java.util.UUID.class),
            rs.getString("vehicle_id"),
            rs.getObject("telemetry_id", java.util.UUID.class),
            AlertType.valueOf(rs.getString("type")),
            AlertSeverity.valueOf(rs.getString("severity")),
            rs.getString("message"),
            rs.getTimestamp("created_at").toInstant()
    );

    private final JdbcClient jdbcClient;

    public AlertRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void saveAll(List<Alert> alerts) {
        alerts.forEach(this::save);
    }

    public List<Alert> findRecent(String vehicleId, AlertType type) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, vehicle_id, telemetry_id, type, severity, message, created_at
                  FROM alerts
                 WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();

        if (vehicleId != null && !vehicleId.isBlank()) {
            sql.append(" AND vehicle_id = ?");
            params.add(vehicleId);
        }
        if (type != null) {
            sql.append(" AND type = ?");
            params.add(type.name());
        }
        sql.append(" ORDER BY created_at DESC LIMIT 100");

        JdbcClient.StatementSpec spec = jdbcClient.sql(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            spec = spec.param(i + 1, params.get(i));
        }

        return spec.query(ROW_MAPPER).list();
    }

    private void save(Alert alert) {
        jdbcClient.sql("""
                INSERT INTO alerts (
                    id, vehicle_id, telemetry_id, type, severity, message, created_at
                )
                VALUES (
                    :id, :vehicleId, :telemetryId, :type, :severity, :message, :createdAt
                )
                """)
                .param("id", alert.id())
                .param("vehicleId", alert.vehicleId())
                .param("telemetryId", alert.telemetryId())
                .param("type", alert.type().name())
                .param("severity", alert.severity().name())
                .param("message", alert.message())
                .param("createdAt", Timestamp.from(alert.createdAt()))
                .update();
    }
}
