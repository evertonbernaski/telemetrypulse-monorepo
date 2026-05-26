CREATE TABLE vehicles (
    id VARCHAR(64) PRIMARY KEY,
    battery_level NUMERIC(5,2) NOT NULL,
    speed NUMERIC(8,2) NOT NULL,
    motor_temperature NUMERIC(8,2) NOT NULL,
    latitude NUMERIC(9,6) NOT NULL,
    longitude NUMERIC(9,6) NOT NULL,
    status VARCHAR(16) NOT NULL,
    last_telemetry_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE telemetry_readings (
    id UUID PRIMARY KEY,
    vehicle_id VARCHAR(64) NOT NULL,
    battery_level NUMERIC(5,2) NOT NULL,
    speed NUMERIC(8,2) NOT NULL,
    motor_temperature NUMERIC(8,2) NOT NULL,
    latitude NUMERIC(9,6) NOT NULL,
    longitude NUMERIC(9,6) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_telemetry_vehicle_occurred_at
    ON telemetry_readings(vehicle_id, occurred_at DESC);

CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    vehicle_id VARCHAR(64) NOT NULL,
    telemetry_id UUID NOT NULL REFERENCES telemetry_readings(id),
    type VARCHAR(64) NOT NULL,
    severity VARCHAR(16) NOT NULL,
    message VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    resolved_at TIMESTAMPTZ
);

CREATE INDEX idx_alerts_vehicle_created_at
    ON alerts(vehicle_id, created_at DESC);

CREATE INDEX idx_alerts_type_created_at
    ON alerts(type, created_at DESC);

CREATE INDEX idx_alerts_active
    ON alerts(resolved_at)
    WHERE resolved_at IS NULL;
