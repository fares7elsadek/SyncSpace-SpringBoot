
import { HttpClient, HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {KeycloakService} from 'keycloak-angular'
import { catchError, from, Observable, switchMap, throttleTime, throwError } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor{
    constructor(
    private keycloakService: KeycloakService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return from(this.keycloakService.getToken()).pipe(
        switchMap(token => {
            if (token) {
            req = req.clone({
                setHeaders: {
                Authorization: `Bearer ${token}`
                }
            });
            }
            return next.handle(req);
        }),
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
            this.keycloakService.login();
            }
            return throwError(() => error);
        })
        );
 }
}
