import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from 'src/app/global';
import { Observable } from 'rxjs';
import {
  UnificationClarificationDto,
  UnificationClarificationResponseDto,
} from '../dtos/unification-clarification-dto';
import { UnificationClarificationFrontendResponseDto } from '../dtos/unification-clarification-selection-dto';

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

  ttlAnalyzeComplete(processId: string): Observable<boolean> {
    return this.httpClient.get<any>(
      `${this.workflowUri}/ttl-analyze-complete/${processId}`
    );
  }

  txtAnalyzeComplete(processId: string): Observable<boolean> {
    return this.httpClient.get<any>(
      `${this.workflowUri}/txt-analyze-complete/${processId}`
    );
  }

  unificationFirstStepComplete(processId: string): Observable<boolean> {
    return this.httpClient.get<any>(
      `${this.workflowUri}/unification-first-step-complete/${processId}`
    );
  }

  triggerUnificationWorkflow(processId: string): Observable<boolean> {
    return this.httpClient.post<boolean>(
      `${this.workflowUri}/trigger-unification-workflow/${processId}`,
      {}
    );
  }

  getUnificationFirstStepResponse(
    processId: string
  ): Observable<UnificationClarificationResponseDto> {
    return this.httpClient.get<UnificationClarificationResponseDto>(
      `${this.workflowUri}/unification-first-step-response/${processId}`
    );
  }

  triggerOutputGeneration(
    processId: string,
    response: UnificationClarificationFrontendResponseDto
  ): Observable<boolean> {
    return this.httpClient.post<boolean>(
      `${this.workflowUri}/trigger-output-generation/${processId}`,
      response
    );
  }

  outputGenerationComplete(processId: string): Observable<boolean> {
    return this.httpClient.get<any>(
      `${this.workflowUri}/output-generation-complete/${processId}`
    );
  }

  getOutputGenerationContent(processId: string): Observable<Blob> {
    return this.httpClient.get(
      `${this.workflowUri}/output-generation-response/${processId}`,
      {
        responseType: 'blob',
      }
    );
  }
}
