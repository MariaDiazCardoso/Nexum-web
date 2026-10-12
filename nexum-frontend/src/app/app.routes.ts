import { Routes } from '@angular/router';
import { LoginPage } from './pages/login/login.page';
import { RegisterPage } from './pages/register/register.page';
import { CatalogPage } from './pages/catalog/catalog.page';
import { authGuard, adminGuard } from './core/auth.guard';
import { AdminPage } from './pages/admin/admin.page';
import { CourseDetailPage } from './pages/course-detail/course-detail.page';
import { MyLearningPage } from './pages/my-learning/my-learning.page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'catalog' },
  { path: 'login', component: LoginPage, title: 'Iniciar sesión | Nexum' },
  { path: 'register', component: RegisterPage, title: 'Crear cuenta | Nexum' },
  { path: 'catalog', component: CatalogPage, canActivate: [authGuard], title: 'Explorar cursos | Nexum' },
  { path: 'courses/:id', component: CourseDetailPage, canActivate: [authGuard], title: 'Curso | Nexum' },
  { path: 'my-learning', component: MyLearningPage, canActivate: [authGuard], title: 'Mis cursos | Nexum' },
  { path: 'admin', component: AdminPage, canActivate: [adminGuard], title: 'Panel administrador | Nexum' },
  { path: '**', redirectTo: 'catalog' },
];
