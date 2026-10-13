import { effect, Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark';
const STORAGE_KEY = 'nexum.theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  /** Current active theme */
  readonly theme = signal<Theme>(this.resolveInitialTheme());

  constructor() {
    // Apply theme to DOM whenever it changes
    effect(() => {
      this.applyTheme(this.theme());
    });
  }

  toggle(): void {
    this.theme.update((t) => (t === 'light' ? 'dark' : 'light'));
  }

  setTheme(theme: Theme): void {
    this.theme.set(theme);
  }

  private resolveInitialTheme(): Theme {
    const stored = globalThis.localStorage?.getItem(STORAGE_KEY) as Theme | null;
    if (stored === 'light' || stored === 'dark') return stored;
    if (globalThis.matchMedia?.('(prefers-color-scheme: dark)').matches) return 'dark';
    return 'light';
  }

  private applyTheme(theme: Theme): void {
    const root = globalThis.document?.documentElement;
    if (!root) return;

    root.setAttribute('data-theme', theme);
    if (theme === 'dark') {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }

    globalThis.localStorage?.setItem(STORAGE_KEY, theme);
  }
}
