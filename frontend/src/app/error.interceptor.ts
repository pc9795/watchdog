import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/internal/Observable';
import {Injectable} from '@angular/core';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/operators';
import {AuthenticationService} from './services/authentication.service';

/**
 * Interceptor to log out in case of 401 unauthorized error.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(public authenticationService: AuthenticationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError(err => {
          if (err.status === 401) {
            // Auto logout if 401 is returned from api.
            this.authenticationService.logout();
            location.reload();
          }
          return throwError(err);
        }
      )
    );
  }
}
