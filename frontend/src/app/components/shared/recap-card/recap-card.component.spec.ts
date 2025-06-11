import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecapCardComponent } from './recap-card.component';

describe('RecapCardComponent', () => {
  let component: RecapCardComponent;
  let fixture: ComponentFixture<RecapCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecapCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecapCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
