package project.backend.workflowmapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a workflow consisting of steps, agents, and variables.
 * This class is used to map the workflow from a TTL file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    List<WorkflowStep> workflowSteps;

    List<WorkflowAgent> workflowAgents;

    List<WorkflowVariable> workflowVariables;
}
