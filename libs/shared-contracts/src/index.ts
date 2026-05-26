export type AlertType = 'SPEED_LIMIT_EXCEEDED' | 'BATTERY_CRITICAL';
export type AlertSeverity = 'WARNING' | 'CRITICAL';

export interface TelemetryIngestRequest {
  vehicleId: string;
  batteryLevel: number;
  speed: number;
  motorTemperature: number;
  latitude: number;
  longitude: number;
  occurredAt?: string;
}

export interface VehicleStatus {
  vehicleId: string;
  batteryLevel: number;
  speed: number;
  motorTemperature: number;
  latitude: number;
  longitude: number;
  status: 'ONLINE' | 'ATTENTION';
  lastTelemetryAt: string;
}

export interface AlertEvent {
  id: string;
  vehicleId: string;
  type: AlertType;
  severity: AlertSeverity;
  message: string;
  createdAt: string;
}

export interface TelemetryEvent {
  vehicle: VehicleStatus;
  alerts: AlertEvent[];
}

export interface RealtimeEnvelope<TPayload> {
  type: 'telemetry' | 'alert';
  payload: TPayload;
}
