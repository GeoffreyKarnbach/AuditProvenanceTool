import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from 'src/app/global';
import { Observable } from 'rxjs';
import { ExampleDto } from '../dtos';

@Injectable({
  providedIn: 'root',
})
export class ExampleService {
  private exampleBaseUri: string = this.globals.backendUri + '/example';

  constructor(private httpClient: HttpClient, private globals: Globals) {}

  /**
   * Example Endpoint
   */
  getExample(): Observable<ExampleDto> {
    return this.httpClient.get<ExampleDto>(this.exampleBaseUri);
  }
}
