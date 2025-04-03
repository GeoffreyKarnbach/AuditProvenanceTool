import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { UserService } from 'src/app/services';
import { Observable } from 'rxjs';
import { Globals } from 'src/app/global';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private userService: UserService, private globals: Globals) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const authUris = [
      this.globals.backendUri + '/user/login',
      this.globals.backendUri + '/user/register',
    ];

    if (req.url === authUris[0] || req.url === authUris[1]) {
      return next.handle(req);
    }

    if (!this.userService.isLoggedIn()) {
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set(
        'Authorization',
        'Bearer ' + this.userService.getToken()
      ),
    });

    return next.handle(authReq);
  }
}
