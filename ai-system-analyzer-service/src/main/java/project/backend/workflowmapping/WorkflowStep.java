package project.backend.workflowmapping;

import lombok.*;

import java.util.List;

/**
 * Represents a step in a workflow.
 * A step has a unique ID, a name, a step number, previous and next step IDs,
 * a list of agents, input and output variables, a potential subtype, and default trace fields.
 * The previous and next step IDs only reference the IDs of the previous and next steps in the workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStep {

    private String id;

    private String stepName;

    private Long stepNumber;

    private String previousStepId;

    private String nextStepId;

    private List<WorkflowAgent> workflowAgents;

    private List<WorkflowVariable> workflowInputVariables;

    private List<WorkflowVariable> workflowOutputVariables;

    private String stepSubtype;

    private List<String> defaultTraceFields;
}
