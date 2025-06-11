package project.backend.workflowmapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a variable in a workflow.
 * A variable has a unique ID, a name, a potential subtype, and a list of default trace fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowVariable {

    private String id;

    private String variableName;

    private String variableSubtype;

    private List<String> defaultTraceFields;
}
