import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { ThemeService } from './core/theme.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly currentYear = new Date().getFullYear();
  protected readonly auth = inject(AuthService);
  protected readonly themeService = inject(ThemeService);
  private readonly router = inject(Router);

  protected readonly isDark = computed(() => this.themeService.theme() === 'dark');
  protected readonly mobileMenuOpen = signal(false);

  protected toggleTheme(): void {
    this.themeService.toggle();
  }

  protected toggleMobileMenu(): void {
    this.mobileMenuOpen.update((open) => !open);
  }

  protected closeMobileMenu(): void {
    this.mobileMenuOpen.set(false);
  }

  protected async logout(): Promise<void> {
    this.auth.logout();
    this.closeMobileMenu();
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
