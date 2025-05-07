package project.backend.workflowmapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"previousStep", "nextStep"})
public class WorkflowStep {

    private String stepName;

    private Long stepNumber;

    // Nur die ID
    @JsonIgnore
    private WorkflowStep previousStep;

    // Nur die ID
    @JsonIgnore
    private WorkflowStep nextStep;

    private List<WorkflowAgent> workflowAgents;

    private List<WorkflowVariable> workflowInputVariables;

    private List<WorkflowVariable> workflowOutputVariables;

    private String stepType;
}
