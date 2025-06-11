package project.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.backend.workflowmapping.Workflow;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnificationClarificationManagingRequestDTO {
    private Workflow workflow;
    private UnificationClarificationResponseDTO unificationClarificationResponse;
}
