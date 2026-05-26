import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, inject } from '@angular/core';
import { AlertEvent, TelemetryEvent, VehicleStatus } from '@telemetrypulse/shared-contracts';
import { BehaviorSubject, Observable, Subject, map, merge, scan, shareReplay } from 'rxjs';

type ConnectionState = 'connecting' | 'connected' | 'disconnected';

@Injectable({ providedIn: 'root' })
export class TelemetryStreamService {
  private readonly http = inject(HttpClient);
  private readonly zone = inject(NgZone);
  private readonly connectionStateSubject = new BehaviorSubject<ConnectionState>('connecting');
  private readonly telemetryEvents = new Subject<TelemetryEvent>();
  private readonly alertEvents = new Subject<AlertEvent>();

  readonly connectionState$ = this.connectionStateSubject.asObservable();

  readonly vehicles$: Observable<VehicleStatus[]> = merge(
    this.http.get<VehicleStatus[]>('/api/vehicles').pipe(map((vehicles) => ({ kind: 'snapshot' as const, vehicles }))),
    this.telemetryEvents.pipe(map((event) => ({ kind: 'update' as const, vehicle: event.vehicle })))
  ).pipe(
    scan((vehicles, event) => {
      if (event.kind === 'snapshot') {
        return event.vehicles;
      }

      const next = new Map(vehicles.map((vehicle) => [vehicle.vehicleId, vehicle]));
      next.set(event.vehicle.vehicleId, event.vehicle);
      return Array.from(next.values()).sort((left, right) => right.lastTelemetryAt.localeCompare(left.lastTelemetryAt));
    }, [] as VehicleStatus[]),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly alerts$: Observable<AlertEvent[]> = merge(
    this.http.get<AlertEvent[]>('/api/alerts').pipe(map((alerts) => ({ kind: 'snapshot' as const, alerts }))),
    this.alertEvents.pipe(map((alert) => ({ kind: 'update' as const, alert })))
  ).pipe(
    scan((alerts, event) => {
      if (event.kind === 'snapshot') {
        return event.alerts;
      }

      return [event.alert, ...alerts.filter((current) => current.id !== event.alert.id)].slice(0, 100);
    }, [] as AlertEvent[]),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  constructor() {
    this.connect();
  }

  private connect(): void {
    const source = new EventSource('/api/stream');

    source.addEventListener('open', () => {
      this.zone.run(() => this.connectionStateSubject.next('connected'));
    });

    source.addEventListener('connected', () => {
      this.zone.run(() => this.connectionStateSubject.next('connected'));
    });

    source.addEventListener('telemetry', (event) => {
      this.zone.run(() => this.telemetryEvents.next(JSON.parse(event.data) as TelemetryEvent));
    });

    source.addEventListener('alert', (event) => {
      this.zone.run(() => this.alertEvents.next(JSON.parse(event.data) as AlertEvent));
    });

    source.addEventListener('error', () => {
      this.zone.run(() => this.connectionStateSubject.next('disconnected'));
    });
  }
}
