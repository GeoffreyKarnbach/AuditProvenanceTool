import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-unification-page',
  templateUrl: './unification-page.component.html',
  styleUrl: './unification-page.component.scss',
})
export class UnificationPageComponent {
  processId!: string;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.processId = this.route.snapshot.paramMap.get('processId')!;
    console.log('Process ID:', this.processId);
  }
}
