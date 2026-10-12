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
    authService = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    globalThis.sessionStorage.clear();
  });

  it('stores the JWT and current user after successful login', () => {
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
    expect(authService.user()?.id).toBe('user-1');
    expect(authService.isAuthenticated()).toBe(true);
  });

  it('clears the session when logging out', () => {
    globalThis.sessionStorage.setItem('nexum.accessToken', 'test-token');
    authService.logout();

    expect(globalThis.sessionStorage.getItem('nexum.accessToken')).toBeNull();
    expect(authService.user()).toBeNull();
    expect(authService.isAuthenticated()).toBe(false);
  });
});
