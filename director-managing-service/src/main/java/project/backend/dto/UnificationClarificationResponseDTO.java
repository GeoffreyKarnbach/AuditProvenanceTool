package project.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnificationClarificationResponseDTO {
    private List<PossibleActivityDto> options;
    private List<MappingItemDto> mappingSuggestions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MappingItemDto {
    private String question;
    private MappingSuggestionDto mapping;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MappingSuggestionDto {
    private String firstTerm;
    private String secondTerm;
    private List<PossibleActivityDto> mappings;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PossibleActivityDto {
    private String activity;
    private List<String> entities;
}