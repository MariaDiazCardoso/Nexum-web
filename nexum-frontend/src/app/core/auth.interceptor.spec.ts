import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from './api.config';
import { authInterceptor } from './auth.interceptor';

@Component({ template: '' })
class LoginStub {}

describe('authInterceptor', () => {
  let http: HttpClient;
  let controller: HttpTestingController;

  beforeEach(() => {
    globalThis.sessionStorage.clear();
    globalThis.sessionStorage.setItem('nexum.accessToken', 'token-viejo');
    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'login', component: LoginStub }]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    controller.verify();
    globalThis.sessionStorage.clear();
  });

  it('adds the Bearer token to protected routes', () => {
    http.get(`${API_BASE_URL}/cursos`).subscribe();
    const req = controller.expectOne(`${API_BASE_URL}/cursos`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-viejo');
    req.flush([]);
  });

  it('does not send the token to /auth routes', () => {
    http.post(`${API_BASE_URL}/auth/token`, {}).subscribe();
    const req = controller.expectOne(`${API_BASE_URL}/auth/token`);
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('clears the session when a protected route answers 401', () => {
    http.get(`${API_BASE_URL}/cursos`).subscribe({ error: () => undefined });
    controller
      .expectOne(`${API_BASE_URL}/cursos`)
      .flush({ message: 'No autenticado' }, { status: 401, statusText: 'Unauthorized' });
    expect(globalThis.sessionStorage.getItem('nexum.accessToken')).toBeNull();
  });
});
