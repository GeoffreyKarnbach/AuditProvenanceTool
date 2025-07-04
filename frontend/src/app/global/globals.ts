import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Globals {
  readonly backendUri: string = environment.directorManagingServiceUri;
}
