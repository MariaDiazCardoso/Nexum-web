import { Routes } from '@angular/router';
import { LoginPage } from './pages/login/login.page';
import { RegisterPage } from './pages/register/register.page';
import { CatalogPage } from './pages/catalog/catalog.page';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'catalog' },
  { path: 'login', component: LoginPage, title: 'Iniciar sesión | Nexum' },
  { path: 'register', component: RegisterPage, title: 'Crear cuenta | Nexum' },
  { path: 'catalog', component: CatalogPage, canActivate: [authGuard], title: 'Explorar cursos | Nexum' },
  { path: '**', redirectTo: 'catalog' },
];
