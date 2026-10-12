import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { App } from './app';

@Component({
  standalone: true,
  template: '<p class="login-route">Inicio de sesión</p>',
})
class RegisterRouteComponent {}

describe('App', () => {
  beforeEach(async () => {
    globalThis.sessionStorage.clear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideRouter([
          { path: 'login', component: RegisterRouteComponent },
          { path: 'register', component: RegisterRouteComponent },
        ]),
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the Nexum navigation', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.brand')?.textContent).toContain('nexum');
    expect(compiled.querySelector('.main-nav a')?.textContent).toContain('Explorar cursos');
  });

  it.each(['USUARIO', 'ADMINISTRADOR'] as const)(
    'clears the %s session and navigates to login when logging out',
    async (role) => {
      globalThis.sessionStorage.setItem('nexum.accessToken', 'test-token');
      globalThis.sessionStorage.setItem('nexum.userRole', role);
      globalThis.sessionStorage.setItem('nexum.userId', `${role.toLowerCase()}-1`);

      const fixture = TestBed.createComponent(App);
      fixture.detectChanges();
      fixture.nativeElement.querySelector('.logout-button').click();
      await fixture.whenStable();

      expect(TestBed.inject(Router).url).toBe('/login');
      expect(fixture.nativeElement.querySelector('.login-route')?.textContent).toContain('Inicio de sesión');
      expect(globalThis.sessionStorage.getItem('nexum.accessToken')).toBeNull();
    },
  );
});
