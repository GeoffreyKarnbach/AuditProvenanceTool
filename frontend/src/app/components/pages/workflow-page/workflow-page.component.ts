import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from 'src/app/services';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-workflow-page',
  templateUrl: './workflow-page.component.html',
  styleUrl: './workflow-page.component.scss',
})
export class WorkflowPageComponent {
  constructor(
    private backendService: BackendService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ttlFile: File | null = null;
  txtFile: File | null = null;

  dragOverTtl = false;
  dragOverTxt = false;

  ttlServerProcessing: boolean = false;
  ttlProcessed: boolean = false;

  txtServerProcessing: boolean = false;
  txtProcessed: boolean = false;

  processId: string | null = null;

  onFileDrop(event: DragEvent, type: 'ttl' | 'txt') {
    event.preventDefault();
    const files = event.dataTransfer?.files;
    if (!files || files.length === 0) return;

    const file = files[0];
    if (type === 'ttl' && file.name.endsWith('.ttl')) {
      this.ttlFile = file;
    } else if (type === 'txt' && file.name.endsWith('.txt')) {
      this.txtFile = file;
    } else {
      alert(`Please drop a valid ${type.toUpperCase()} file.`);
    }

    this.dragOverTtl = false;
    this.dragOverTxt = false;
  }

  onDragOver(event: DragEvent, type: 'ttl' | 'txt') {
    event.preventDefault();
    if (type === 'ttl') this.dragOverTtl = true;
    if (type === 'txt') this.dragOverTxt = true;
  }

  onDragLeave(type: 'ttl' | 'txt') {
    if (type === 'ttl') this.dragOverTtl = false;
    if (type === 'txt') this.dragOverTxt = false;
  }

  onSubmit() {
    if (!this.ttlFile || !this.txtFile) {
      this.toastService.showError('Please upload both TTL and TXT files.');
      return;
    }

    this.backendService.uploadFiles(this.ttlFile, this.txtFile).subscribe({
      next: (response) => {
        this.toastService.showSuccess(
          'Files uploaded successfully!',
          'Success'
        );
        this.ttlServerProcessing = true;
        this.txtServerProcessing = true;
        this.processId = response;

        const intervalTtl = setInterval(() => {
          this.backendService.ttlAnalyzeComplete(response).subscribe({
            next: (isComplete) => {
              if (isComplete) {
                clearInterval(intervalTtl);
                this.ttlServerProcessing = false;
                this.ttlProcessed = true;
              }
            },
            error: (error) => {
              console.error('Error checking analysis status:', error);
            },
          });
        }, 1000);

        const intervalTxt = setInterval(() => {
          this.backendService.txtAnalyzeComplete(response).subscribe({
            next: (isComplete) => {
              if (isComplete) {
                clearInterval(intervalTxt);
                this.txtServerProcessing = false;
                this.txtProcessed = true;
              }
            },
            error: (error) => {
              console.error('Error checking analysis status:', error);
            },
          });
        }, 1000);
      },
      error: (error) => {
        console.error('Error uploading files:', error);
        this.toastService.showError('Error uploading files. Please try again.');
      },
    });
  }

  goToUnification() {
    if (this.ttlProcessed && this.txtProcessed) {
      this.router.navigate(['/unification', this.processId]);
    } else {
      this.toastService.showError(
        'Please wait for both files to be processed before proceeding to unification.'
      );
    }
  }
}
