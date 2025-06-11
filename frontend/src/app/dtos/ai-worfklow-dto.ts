export interface WorkflowDTO {
  workflowSteps: WorkflowStepDTO[];
  workflowAgents: WorkflowAgentDTO[];
  workflowVariables: WorkflowVariableDTO[];
}

export interface WorkflowStepDTO {
  id: string;
  stepName: string;
  stepNumber: number;
  previousStepId: string;
  nextStepId: string;
  workflowAgents: WorkflowAgentDTO[];
  workflowInputVariables: WorkflowVariableDTO[];
  workflowOutputVariables: WorkflowVariableDTO[];
  stepSubtype: string;
  defaultTraceFields: string[];
}

export interface WorkflowAgentDTO {
  id: string;
  agentName: string;
  defaultTraceFields: string[];
}

export interface WorkflowVariableDTO {
  id: string;
  variableName: string;
  variableSubtype: string;
  defaultTraceFields: string[];
}
