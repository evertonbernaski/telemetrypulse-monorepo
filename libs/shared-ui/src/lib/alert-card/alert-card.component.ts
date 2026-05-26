import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { AlertEvent } from '@telemetrypulse/shared-contracts';

@Component({
  selector: 'tp-alert-card',
  standalone: true,
  imports: [DatePipe, NgClass],
  template: `
    <article class="alert-card new-alert" [ngClass]="alert.severity.toLowerCase()">
      <div class="alert-card__header">
        <strong>{{ title }}</strong>
        <span>{{ alert.createdAt | date:'HH:mm:ss' }}</span>
      </div>
      <p>{{ alert.message }}</p>
      <small>{{ alert.vehicleId }}</small>
    </article>
  `,
  styles: [`
    .alert-card {
      border: 1px solid var(--panel-border);
      border-left-width: 4px;
      border-radius: 8px;
      background: var(--panel);
      padding: 12px;
      animation: highlight 1.2s ease-out;
    }

    .alert-card.warning {
      border-left-color: var(--warning);
      background: var(--warning-bg);
    }

    .alert-card.critical {
      border-left-color: var(--critical);
      background: var(--critical-bg);
    }

    .alert-card__header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 8px;
      margin-bottom: 6px;
    }

    .alert-card__header span,
    small {
      color: var(--muted);
      font-size: 0.78rem;
    }

    p {
      margin: 0 0 8px;
      line-height: 1.35;
    }

    @keyframes highlight {
      0% {
        transform: translateY(-2px);
        box-shadow: 0 0 0 3px rgba(15, 118, 110, 0.2);
      }
      100% {
        transform: translateY(0);
        box-shadow: none;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AlertCardComponent {
  @Input({ required: true }) alert!: AlertEvent;

  get title(): string {
    return this.alert.type === 'SPEED_LIMIT_EXCEEDED' ? 'Excesso de velocidade' : 'Bateria crítica';
  }
}
