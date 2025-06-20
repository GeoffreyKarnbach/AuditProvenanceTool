package project.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnificationClarificationFrontendResponseDTO {
    private String requestId;
    private List<UnificationClarificationSelectionItemDto> items;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnificationClarificationSelectionItemDto {
    private String question;
    private String selectedActivityId;
    private String selectedEntityId;
    private String selectedTraceValue;

}