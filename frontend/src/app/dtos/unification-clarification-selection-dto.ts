export interface UnificationClarificationSelectionItemDto {
  question: string;
  selectedActivityId: string;
  selectedEntityId: string;
  selectedTraceValue: string | null;
}

export interface UnificationClarificationFrontendResponseDto {
  requestId: string;
  items: UnificationClarificationSelectionItemDto[];
}
