import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  UnificationClarificationFrontendResponseDto,
  UnificationClarificationSelectionItemDto,
} from 'src/app/dtos/unification-clarification-selection-dto';

@Component({
  selector: 'app-recap-card',
  templateUrl: './recap-card.component.html',
  styleUrl: './recap-card.component.scss',
})
export class RecapCardComponent {
  @Input() selections: UnificationClarificationSelectionItemDto[] = [];
  @Input() requestId: string = '';

  @Output()
  submitEventEmitter: EventEmitter<UnificationClarificationFrontendResponseDto> =
    new EventEmitter<UnificationClarificationFrontendResponseDto>();

  get allSelected(): boolean {
    return this.selections.every(
      (item) => item.selectedActivityId && item.selectedEntityId
    );
  }

  submitSelections(): void {
    const response: UnificationClarificationFrontendResponseDto = {
      requestId: this.requestId,
      items: this.selections,
    };

    this.submitEventEmitter.emit(response);
  }
}
