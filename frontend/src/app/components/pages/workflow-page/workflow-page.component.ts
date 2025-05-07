import { Component, OnInit } from '@angular/core';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-workflow-page',
  templateUrl: './workflow-page.component.html',
  styleUrl: './workflow-page.component.scss',
})
export class WorkflowPageComponent {
  constructor(private backendService: BackendService) {}

  ttlFile: File | null = null;
  txtFile: File | null = null;

  dragOverTtl = false;
  dragOverTxt = false;

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
      alert('Please upload both TTL and TXT files.');
      return;
    }

    this.backendService.uploadFiles(this.ttlFile, this.txtFile).subscribe({
      next: (response) => {
        console.log('Files uploaded successfully:', response);
        alert('Files uploaded successfully!');
      },
      error: (error) => {
        console.error('Error uploading files:', error);
        alert('Error uploading files. Please try again.');
      },
    });
  }
}
