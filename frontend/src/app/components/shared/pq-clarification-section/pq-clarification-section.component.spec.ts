import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PqClarificationSectionComponent } from './pq-clarification-section.component';

describe('PqClarificationSectionComponent', () => {
  let component: PqClarificationSectionComponent;
  let fixture: ComponentFixture<PqClarificationSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PqClarificationSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PqClarificationSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
