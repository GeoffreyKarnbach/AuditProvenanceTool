import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from 'src/app/global';
import { map, Observable, tap } from 'rxjs';
import { LoginRegisterDto } from '../dtos';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private userBaseUri: string = this.globals.backendUri + '/user';

  constructor(
    private toastService: ToastService,
    private httpClient: HttpClient,
    private globals: Globals
  ) {}

  register(loginRegisterDto: LoginRegisterDto): Observable<string> {
    return this.httpClient
      .post<string>(this.userBaseUri + '/register', loginRegisterDto, {
        responseType: 'text' as 'json',
      })
      .pipe(
        tap((authResponse: string) => this.saveCurrentUserToken(authResponse))
      );
  }

  login(loginRegisterDto: LoginRegisterDto): Observable<string> {
    return this.httpClient
      .post<string>(this.userBaseUri + '/login', loginRegisterDto, {
        responseType: 'text' as 'json',
      })
      .pipe(
        tap((authResponse: string) => this.saveCurrentUserToken(authResponse))
      );
  }

  deleteAccount(): Observable<void> {
    return this.httpClient.delete<void>(this.userBaseUri + '/delete').pipe(
      tap(() => {
        localStorage.removeItem('token');
      })
    );
  }

  saveCurrentUserToken(token: string): void {
    token = token.split(' ')[1];
    localStorage.setItem('token', token);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.toastService.showSuccess('Successfully logged out', 'Success');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string {
    let token = localStorage.getItem('token');
    if (!token) {
      return '';
    }
    return token;
  }
}
