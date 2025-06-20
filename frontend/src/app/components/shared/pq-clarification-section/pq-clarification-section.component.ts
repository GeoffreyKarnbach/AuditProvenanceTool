import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { UnificationClarificationDto } from 'src/app/dtos/unification-clarification-dto';
import {
  UnificationClarificationFrontendResponseDto,
  UnificationClarificationSelectionItemDto,
} from 'src/app/dtos/unification-clarification-selection-dto';

@Component({
  selector: 'app-pq-clarification-section',
  templateUrl: './pq-clarification-section.component.html',
  styleUrl: './pq-clarification-section.component.scss',
})
export class PqClarificationSectionComponent implements OnChanges {
  @Output()
  submitEventEmitter: EventEmitter<UnificationClarificationFrontendResponseDto> =
    new EventEmitter<UnificationClarificationFrontendResponseDto>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clarificationRequests']) {
      this.selections = this.clarificationRequests.mappingSuggestions.map(
        (suggestion) => ({
          question: suggestion.question,
          selectedActivityId: '',
          selectedEntityId: '',
          selectedTraceValue: '',
        })
      );
    }
  }

  @Input() clarificationRequests: UnificationClarificationDto = {
    options: [],
    mappingSuggestions: [],
    requestId: '',
  };

  selections: UnificationClarificationSelectionItemDto[] = [];

  currentIndex = 0;

  prevCard(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  nextCard(): void {
    if (
      this.currentIndex < this.clarificationRequests.mappingSuggestions.length
    ) {
      this.currentIndex++;
    }
  }

  emitSelection(event: UnificationClarificationFrontendResponseDto): void {
    this.submitEventEmitter.emit(event);
  }
}
