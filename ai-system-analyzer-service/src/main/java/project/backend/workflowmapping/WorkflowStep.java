package project.backend.workflowmapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStep {

    private String stepName;

    private Long stepNumber;

    private WorkflowStep previousStep;

    private WorkflowStep nextStep;

    private List<WorkflowAgent> workflowAgents;

    private List<WorkflowVariable> workflowInputVariables;

    private List<WorkflowVariable> workflowOutputVariables;
}
