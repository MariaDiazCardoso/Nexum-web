import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { LoginRequest, LoginResponse, RegisterRequest, UserResponse } from './api.models';

const TOKEN_STORAGE_KEY = 'nexum.accessToken';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly user = signal<UserResponse | null>(null);
  readonly isAuthenticated = signal(Boolean(globalThis.sessionStorage?.getItem(TOKEN_STORAGE_KEY)));

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/auth/token`, request).pipe(
      tap((response) => {
        globalThis.sessionStorage?.setItem(TOKEN_STORAGE_KEY, response.token);
        this.user.set(response.usuario);
        this.isAuthenticated.set(true);
      }),
    );
  }

  register(request: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/auth/registro`, request);
  }

  logout(): void {
    globalThis.sessionStorage?.removeItem(TOKEN_STORAGE_KEY);
    this.user.set(null);
    this.isAuthenticated.set(false);
  }
}
