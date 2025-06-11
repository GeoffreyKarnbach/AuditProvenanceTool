import { Component, Input } from '@angular/core';
import {
  MappingItemDto,
  PossibleActivitiyDto,
} from 'src/app/dtos/unification-clarification-dto';
import { UnificationClarificationSelectionItemDto } from 'src/app/dtos/unification-clarification-selection-dto';

@Component({
  selector: 'app-pq-clarification-card',
  templateUrl: './pq-clarification-card.component.html',
  styleUrl: './pq-clarification-card.component.scss',
})
export class PqClarificationCardComponent {
  @Input() possibleActivities: PossibleActivitiyDto[] = [];
  @Input() mapping!: MappingItemDto;
  @Input() selections: UnificationClarificationSelectionItemDto[] = [];

  availableActivities: PossibleActivitiyDto[] = [];
  availableEntities: string[] = [];

  selectedActivityId: string | null = null;
  selectedEntityId: string | null = null;

  suggestionOverride: boolean = false;

  defaultEntitiesSuggested: boolean = false;

  ngOnInit(): void {
    if (this.mapping.mapping?.mappings?.length) {
      this.availableActivities = this.mapping.mapping.mappings;
    } else {
      this.availableActivities = this.possibleActivities;
    }

    if (this.availableActivities.length === 1) {
      this.selectedActivityId = this.availableActivities[0].activity;
      this.onActivityChange();
    }
  }

  onOverrideSuggestion(): void {
    this.suggestionOverride = !this.suggestionOverride;
    if (this.suggestionOverride) {
      this.availableActivities = this.possibleActivities;
    } else {
      this.availableActivities = this.mapping.mapping?.mappings || [];
    }
  }

  onActivityChange(): void {
    const source =
      this.mapping.mapping &&
      this.mapping.mapping.mappings &&
      this.mapping.mapping.mappings.length > 0
        ? this.mapping.mapping.mappings
        : this.possibleActivities;

    const activityObj = source.find(
      (a) => a.activity === this.selectedActivityId
    );

    if (activityObj?.entities.length !== 0) {
      this.availableEntities = activityObj?.entities || [];
    } else {
      this.defaultEntitiesSuggested = true;
      this.availableEntities =
        this.possibleActivities
          .find((a) => a.activity === this.selectedActivityId)
          ?.entities.filter((e) => e !== null) || [];
      this.selectedEntityId = null;
    }
  }

  resetActivitySelection(): void {
    this.selectedActivityId = null;
    this.selectedEntityId = null;
    this.availableEntities = [];
    this.defaultEntitiesSuggested = false;

    const selection = this.selections.find(
      (s) => s.question === this.mapping.question
    );
    if (selection) {
      selection.selectedActivityId = '';
      selection.selectedEntityId = '';
    }

    console.log('Selection reset:', this.selections);
  }

  onEntityChange(): void {
    const selection = this.selections.find(
      (s) => s.question === this.mapping.question
    );
    if (selection) {
      selection.selectedActivityId = this.selectedActivityId || '';
      selection.selectedEntityId = this.selectedEntityId || '';
      console.log('Selection updated:', this.selections);
    }
  }
}
