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

  uploadFiles(ttlFile: File, txtFile: File): Observable<string> {
    const formData = new FormData();
    formData.append('ttlFile', ttlFile);
    formData.append('txtFile', txtFile);

    return this.httpClient.post<string>(this.workflowUri, formData, {
      headers: {},
      responseType: 'text' as 'json',
    });
  }
}
