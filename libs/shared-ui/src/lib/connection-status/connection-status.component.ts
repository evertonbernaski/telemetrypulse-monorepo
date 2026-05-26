import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

type ConnectionState = 'connecting' | 'connected' | 'disconnected';

@Component({
  selector: 'tp-connection-status',
  standalone: true,
  imports: [NgClass],
  template: `
    <span class="connection" [ngClass]="state">
      <span class="dot"></span>
      {{ label }}
    </span>
  `,
  styles: [`
    .connection {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      border: 1px solid var(--panel-border);
      border-radius: 999px;
      background: var(--panel);
      color: var(--muted);
      font-size: 0.88rem;
      font-weight: 700;
      padding: 8px 12px;
      white-space: nowrap;
    }

    .dot {
      width: 9px;
      height: 9px;
      border-radius: 999px;
      background: currentColor;
    }

    .connected {
      color: var(--accent);
    }

    .connecting {
      color: var(--warning);
    }

    .disconnected {
      color: var(--critical);
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConnectionStatusComponent {
  @Input({ required: true }) state: ConnectionState = 'connecting';

  get label(): string {
    const labels: Record<ConnectionState, string> = {
      connecting: 'Conectando',
      connected: 'Tempo real ativo',
      disconnected: 'Reconectando'
    };

    return labels[this.state];
  }
}
