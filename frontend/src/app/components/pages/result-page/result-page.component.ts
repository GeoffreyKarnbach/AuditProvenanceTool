import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService, ToastService } from 'src/app/services';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrl: './result-page.component.scss',
})
export class ResultPageComponent implements OnInit {
  processId!: string;

  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private backendService: BackendService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.processId = this.route.snapshot.paramMap.get('processId')!;
    const intervalCompleteCheck = setInterval(() => {
      this.backendService.outputGenerationComplete(this.processId).subscribe({
        next: (isComplete) => {
          if (isComplete) {
            this.toastService.showSuccess(
              'Output generation completed successfully.',
              'Success'
            );
            clearInterval(intervalCompleteCheck);
            this.triggerDownload();
            this.loading = false;
          }
        },
        error: (error) => {
          this.toastService.showErrorResponse(error);
          this.loading = false;
        },
      });
    }, 1000);
  }

  triggerDownload(): void {
    this.backendService
      .getOutputGenerationContent(this.processId)
      .subscribe((zipBlob) => {
        const blob = new Blob([zipBlob], { type: 'application/zip' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'output.zip';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      });
  }
}
