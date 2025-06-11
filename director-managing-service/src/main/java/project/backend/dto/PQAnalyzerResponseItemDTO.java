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
public class PQAnalyzerResponseItemDTO {
    private String original_question;
    private List<List<String>> tokens;
    private List<Object> patterns;
    private String rewritten_question;
    private List<String> formalized_question;
    private List<Object> target_concept;
    private Map<String, String> mapped_to_prov_o;
}
