import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { AuthService } from './auth.service';

const TOKEN_STORAGE_KEY = 'nexum.accessToken';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Registro y login son públicos: nunca llevan token (uno vencido los bloquearía).
  const isAuthRoute = request.url.startsWith(`${API_BASE_URL}/auth/`);
  const token = globalThis.sessionStorage?.getItem(TOKEN_STORAGE_KEY);

  const outgoing =
    token && !isAuthRoute
      ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : request;

  return next(outgoing).pipe(
    catchError((error: unknown) => {
      // Token vencido o inválido en una ruta protegida: cerrar sesión y volver al login.
      if (error instanceof HttpErrorResponse && error.status === 401 && !isAuthRoute && token) {
        auth.logout();
        void router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
  );
};
