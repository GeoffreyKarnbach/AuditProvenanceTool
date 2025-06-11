import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UnificationClarificationResponseDto } from 'src/app/dtos/unification-clarification-dto';
import { BackendService, ToastService } from 'src/app/services';
import * as devMock from 'src/assets/devMock.json';

@Component({
  selector: 'app-unification-page',
  templateUrl: './unification-page.component.html',
  styleUrl: './unification-page.component.scss',
})
export class UnificationPageComponent {
  processId!: string;

  mode: string = 'DEV';

  unificationFirstStepComplete: boolean = false;
  unificationFirstStepResponse: UnificationClarificationResponseDto | undefined;

  constructor(
    private route: ActivatedRoute,
    private backendService: BackendService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.mode === 'DEV') {
      this.processId = 'dev-process-id';
      this.unificationFirstStepComplete = true;
      this.unificationFirstStepResponse =
        devMock as UnificationClarificationResponseDto;
      console.log(this.unificationFirstStepResponse.workflow);
      return;
    }

    this.processId = this.route.snapshot.paramMap.get('processId')!;
    console.log('Process ID:', this.processId);
    this.backendService.triggerUnificationWorkflow(this.processId).subscribe({
      next: (value) => {
        if (value) {
          this.toastService.showSuccess(
            'Unification workflow triggered successfully.'
          );

          const intervalTxt = setInterval(() => {
            this.backendService
              .unificationFirstStepComplete(this.processId)
              .subscribe({
                next: (isComplete) => {
                  if (isComplete) {
                    clearInterval(intervalTxt);
                    this.backendService
                      .getUnificationFirstStepResponse(this.processId)
                      .subscribe({
                        next: (response) => {
                          console.log(
                            'Unification first step response:',
                            response
                          );
                          this.unificationFirstStepComplete = true;
                        },
                        error: (error) => {
                          console.error(
                            'Error fetching unification first step response:',
                            error
                          );
                          this.toastService.showError(
                            'Failed to fetch unification first step response.'
                          );
                          this.router.navigate(['/']);
                        },
                      });
                  }
                },
                error: (error) => {
                  console.error('Error checking analysis status:', error);
                },
              });
          }, 1000);
        } else {
          this.toastService.showError(
            'Failed to trigger unification workflow, please try again.'
          );
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        this.toastService.showError(
          'An error occurred while triggering the unification workflow: ' +
            error.message
        );
        this.router.navigate(['/']);
      },
    });
  }
}
