import { Component, Input } from '@angular/core';
import { UnificationClarificationSelectionItemDto } from 'src/app/dtos/unification-clarification-selection-dto';

@Component({
  selector: 'app-recap-card',
  templateUrl: './recap-card.component.html',
  styleUrl: './recap-card.component.scss',
})
export class RecapCardComponent {
  @Input() selections: UnificationClarificationSelectionItemDto[] = [];
}
