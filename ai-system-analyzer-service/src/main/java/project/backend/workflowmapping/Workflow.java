package project.backend.workflowmapping;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    List<WorkflowStep> workflowSteps;

}
