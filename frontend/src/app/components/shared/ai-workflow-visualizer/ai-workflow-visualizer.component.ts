import { Component, Input } from '@angular/core';
import { WorkflowDTO } from 'src/app/dtos/ai-worfklow-dto';

@Component({
  selector: 'app-ai-workflow-visualizer',
  templateUrl: './ai-workflow-visualizer.component.html',
  styleUrl: './ai-workflow-visualizer.component.scss',
})
export class AiWorkflowVisualizerComponent {
  @Input() workflow: WorkflowDTO = {
    workflowSteps: [],
    workflowAgents: [],
    workflowVariables: [],
  };
}
