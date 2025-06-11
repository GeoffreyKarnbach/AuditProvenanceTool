import { WorkflowDTO } from './ai-worfklow-dto';

export interface UnificationClarificationDto {
  options: PossibleActivitiyDto[];
  mappingSuggestions: MappingItemDto[];
}

export interface MappingItemDto {
  question: string;
  mapping?: MappingSuggestionDto;
}

export interface MappingSuggestionDto {
  firstTerm: string;
  secondTerm: string;
  mappings: PossibleActivitiyDto[];
}

export interface PossibleActivitiyDto {
  activity: string;
  entities: string[];
}

export interface UnificationClarificationResponseDto {
  workflow: WorkflowDTO;
  unificationClarificationResponse: UnificationClarificationDto;
}
