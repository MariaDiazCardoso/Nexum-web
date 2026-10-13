import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { LoginRequest, LoginResponse, RegisterRequest, UserResponse } from './api.models';

const TOKEN_STORAGE_KEY = 'nexum.accessToken';
const ROLE_STORAGE_KEY = 'nexum.userRole';
const USER_ID_STORAGE_KEY = 'nexum.userId';
type UserRole = UserResponse['rol'];

function getStoredRole(): UserRole | null {
  const role = globalThis.sessionStorage?.getItem(ROLE_STORAGE_KEY);
  return role === 'USUARIO' || role === 'ADMINISTRADOR' ? role : null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly user = signal<UserResponse | null>(null);
  readonly isAuthenticated = signal(Boolean(globalThis.sessionStorage?.getItem(TOKEN_STORAGE_KEY)));
  readonly role = signal<UserRole | null>(getStoredRole());
  readonly userId = signal<string | null>(globalThis.sessionStorage?.getItem(USER_ID_STORAGE_KEY) ?? null);
  readonly isAdmin = computed(() => this.role() === 'ADMINISTRADOR');

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/auth/token`, request).pipe(
      tap((response) => {
        globalThis.sessionStorage?.setItem(TOKEN_STORAGE_KEY, response.token);
        globalThis.sessionStorage?.setItem(ROLE_STORAGE_KEY, response.usuario.rol);
        globalThis.sessionStorage?.setItem(USER_ID_STORAGE_KEY, response.usuario.id);
        this.user.set(response.usuario);
        this.role.set(response.usuario.rol);
        this.userId.set(response.usuario.id);
        this.isAuthenticated.set(true);
      }),
    );
  }

  register(request: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/auth/registro`, request);
  }


  logout(): void {
    globalThis.sessionStorage?.removeItem(TOKEN_STORAGE_KEY);
    globalThis.sessionStorage?.removeItem(ROLE_STORAGE_KEY);
    globalThis.sessionStorage?.removeItem(USER_ID_STORAGE_KEY);
    this.user.set(null);
    this.role.set(null);
    this.userId.set(null);
    this.isAuthenticated.set(false);
  }
}
