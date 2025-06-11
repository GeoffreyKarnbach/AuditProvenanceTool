import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PqClarificationCardComponent } from './pq-clarification-card.component';

describe('PqClarificationCardComponent', () => {
  let component: PqClarificationCardComponent;
  let fixture: ComponentFixture<PqClarificationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PqClarificationCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PqClarificationCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
