import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from 'src/app/global';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BackendService {
  private workflowUri: string = this.globals.backendUri + '/workflow';

  constructor(private httpClient: HttpClient, private globals: Globals) {}
}
