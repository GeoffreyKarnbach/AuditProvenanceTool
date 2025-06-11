import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiWorkflowVisualizerComponent } from './ai-workflow-visualizer.component';

describe('AiWorkflowVisualizerComponent', () => {
  let component: AiWorkflowVisualizerComponent;
  let fixture: ComponentFixture<AiWorkflowVisualizerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AiWorkflowVisualizerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AiWorkflowVisualizerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
