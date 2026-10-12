import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from './api.config';
import { AuthService } from './auth.service';
import { LoginResponse } from './api.models';

describe('AuthService', () => {
  let authService: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    globalThis.sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    globalThis.sessionStorage.clear();
  });

  it('stores the JWT and current user after successful login', () => {
    authService = TestBed.inject(AuthService);
    const response: LoginResponse = {
      token: 'test-token',
      tipo: 'Bearer',
      expiraEn: 1800,
      usuario: {
        id: 'user-1',
        nombre: 'Valeria',
        apellido: 'Nexum',
        email: 'valeria@example.com',
        rol: 'USUARIO',
        puntosXp: 0,
        avatarUrl: null,
        fechaRegistro: '2026-10-12T10:00:00',
        ultimaConexion: null,
        emailVerificado: false,
      },
    };

    authService.login({ email: 'valeria@example.com', password: 'password123' }).subscribe();

    const request = httpTestingController.expectOne(`${API_BASE_URL}/auth/token`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ email: 'valeria@example.com', password: 'password123' });
    request.flush(response);

    expect(globalThis.sessionStorage.getItem('nexum.accessToken')).toBe('test-token');
    expect(globalThis.sessionStorage.getItem('nexum.userRole')).toBe('USUARIO');
    expect(globalThis.sessionStorage.getItem('nexum.userId')).toBe('user-1');
    expect(authService.user()?.id).toBe('user-1');
    expect(authService.userId()).toBe('user-1');
    expect(authService.role()).toBe('USUARIO');
    expect(authService.isAdmin()).toBe(false);
    expect(authService.isAuthenticated()).toBe(true);
  });

  it('restores the administrator role from the current browser session', () => {
    globalThis.sessionStorage.setItem('nexum.accessToken', 'admin-token');
    globalThis.sessionStorage.setItem('nexum.userRole', 'ADMINISTRADOR');
    globalThis.sessionStorage.setItem('nexum.userId', 'admin-1');

    const restoredAuthService = TestBed.inject(AuthService);

    expect(restoredAuthService.isAuthenticated()).toBe(true);
    expect(restoredAuthService.isAdmin()).toBe(true);
    expect(restoredAuthService.userId()).toBe('admin-1');
  });

  it('clears the session when logging out', () => {
    globalThis.sessionStorage.setItem('nexum.accessToken', 'test-token');
    globalThis.sessionStorage.setItem('nexum.userRole', 'ADMINISTRADOR');
    globalThis.sessionStorage.setItem('nexum.userId', 'admin-1');
    authService = TestBed.inject(AuthService);
    authService.logout();

    expect(globalThis.sessionStorage.getItem('nexum.accessToken')).toBeNull();
    expect(globalThis.sessionStorage.getItem('nexum.userRole')).toBeNull();
    expect(globalThis.sessionStorage.getItem('nexum.userId')).toBeNull();
    expect(authService.user()).toBeNull();
    expect(authService.role()).toBeNull();
    expect(authService.userId()).toBeNull();
    expect(authService.isAuthenticated()).toBe(false);
  });
});
