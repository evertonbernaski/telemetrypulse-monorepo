import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { AsyncPipe, DatePipe, DecimalPipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { combineLatest, map, startWith } from 'rxjs';
import { AlertCardComponent, ConnectionStatusComponent } from '@telemetrypulse/shared-ui';
import { AlertType } from '@telemetrypulse/shared-contracts';
import { TelemetryStreamService } from './telemetry-stream.service';

@Component({
  selector: 'tp-root',
  standalone: true,
  imports: [AsyncPipe, DatePipe, DecimalPipe, FormsModule, NgClass, AlertCardComponent, ConnectionStatusComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  private readonly telemetryStream = inject(TelemetryStreamService);

  readonly alertType = signal<AlertType | ''>('');
  readonly vehicleId = signal('');

  readonly vehicles$ = this.telemetryStream.vehicles$;
  readonly connectionState$ = this.telemetryStream.connectionState$;

  readonly filteredAlerts$ = combineLatest([
    this.telemetryStream.alerts$,
    toObservable(this.alertType),
    toObservable(this.vehicleId)
  ]).pipe(
    map(([alerts, type, vehicleId]) => {
      const normalizedVehicleId = vehicleId.trim().toLowerCase();
      return alerts.filter((alert) => {
        const matchesType = !type || alert.type === type;
        const matchesVehicle = !normalizedVehicleId || alert.vehicleId.toLowerCase().includes(normalizedVehicleId);
        return matchesType && matchesVehicle;
      });
    })
  );

  readonly vehicleCount$ = this.vehicles$.pipe(map((vehicles) => vehicles.length), startWith(0));
  readonly activeFilterCount = computed(() => Number(Boolean(this.alertType())) + Number(Boolean(this.vehicleId().trim())));

  trackVehicle(_: number, vehicle: { vehicleId: string }): string {
    return vehicle.vehicleId;
  }

  clearFilters(): void {
    this.alertType.set('');
    this.vehicleId.set('');
  }
}
