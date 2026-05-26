import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlertCardComponent } from './alert-card.component';

describe(AlertCardComponent.name, () => {
  let fixture: ComponentFixture<AlertCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AlertCardComponent);
  });

  it('renders speed alert details', () => {
    fixture.componentRef.setInput('alert', {
      id: '4f31b582-2b8e-4832-b85c-c799d76480db',
      vehicleId: 'EV-001',
      type: 'SPEED_LIMIT_EXCEEDED',
      severity: 'WARNING',
      message: 'Excesso de velocidade acima de 120 km/h',
      createdAt: '2026-05-26T12:00:00Z'
    });

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Excesso de velocidade');
    expect(fixture.nativeElement.textContent).toContain('EV-001');
  });
});
