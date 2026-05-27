import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { TelemetryStreamService } from './telemetry-stream.service';

describe(AppComponent.name, () => {
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        {
          provide: TelemetryStreamService,
          useValue: {
            connectionState$: of('connected'),
            vehicles$: of([
              {
                vehicleId: 'EV-001',
                batteryLevel: 80,
                speed: 45,
                motorTemperature: 66,
                latitude: -23.5505,
                longitude: -46.6333,
                status: 'ONLINE',
                lastTelemetryAt: '2026-05-26T12:00:00Z'
              }
            ]),
            alerts$: of([])
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
  });

  it('renders active vehicles from the stream state', () => {
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Fleet Dashboard');
    expect(fixture.nativeElement.textContent).toContain('EV-001');
    expect(fixture.nativeElement.textContent).toContain('Tempo real ativo');
  });
});
