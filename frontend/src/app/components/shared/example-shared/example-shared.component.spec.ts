import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExampleSharedComponent } from './example-shared.component';

describe('ExampleSharedComponent', () => {
  let component: ExampleSharedComponent;
  let fixture: ComponentFixture<ExampleSharedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExampleSharedComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExampleSharedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
