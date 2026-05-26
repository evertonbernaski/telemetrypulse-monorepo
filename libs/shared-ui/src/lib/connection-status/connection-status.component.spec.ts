import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConnectionStatusComponent } from './connection-status.component';

describe(ConnectionStatusComponent.name, () => {
  let fixture: ComponentFixture<ConnectionStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionStatusComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ConnectionStatusComponent);
  });

  it('renders connected state label', () => {
    fixture.componentRef.setInput('state', 'connected');

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Tempo real ativo');
  });
});
