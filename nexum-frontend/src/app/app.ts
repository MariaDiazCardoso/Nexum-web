import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly currentYear = new Date().getFullYear();
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected async logout(): Promise<void> {
    this.auth.logout();
    try {
      const navigated = await this.router.navigateByUrl('/login', { replaceUrl: true });
      if (!navigated) {
        globalThis.location.replace('/login');
      }
    } catch (error) {
      console.error('No se pudo navegar al inicio de sesión después de cerrar sesión.', error);
      globalThis.location.replace('/login');
    }
  }
}
