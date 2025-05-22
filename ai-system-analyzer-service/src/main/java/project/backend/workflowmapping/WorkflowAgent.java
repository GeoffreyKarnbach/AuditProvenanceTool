package project.backend.workflowmapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents an agent in a workflow.
 * An agent has a unique ID, a name, and a list of default trace fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowAgent {

    public String id;

    private String agentName;

    private List<String> defaultTraceFields;

}
