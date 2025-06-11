import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { UnificationClarificationDto } from 'src/app/dtos/unification-clarification-dto';
import { UnificationClarificationSelectionItemDto } from 'src/app/dtos/unification-clarification-selection-dto';

@Component({
  selector: 'app-pq-clarification-section',
  templateUrl: './pq-clarification-section.component.html',
  styleUrl: './pq-clarification-section.component.scss',
})
export class PqClarificationSectionComponent implements OnChanges {
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clarificationRequests']) {
      this.selections = this.clarificationRequests.mappingSuggestions.map(
        (suggestion) => ({
          question: suggestion.question,
          selectedActivityId: '',
          selectedEntityId: '',
        })
      );
    }
  }

  @Input() clarificationRequests: UnificationClarificationDto = {
    options: [],
    mappingSuggestions: [],
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
}
