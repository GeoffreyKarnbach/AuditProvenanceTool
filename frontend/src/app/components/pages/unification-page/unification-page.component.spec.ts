import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnificationPageComponent } from './unification-page.component';

describe('UnificationPageComponent', () => {
  let component: UnificationPageComponent;
  let fixture: ComponentFixture<UnificationPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnificationPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnificationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
