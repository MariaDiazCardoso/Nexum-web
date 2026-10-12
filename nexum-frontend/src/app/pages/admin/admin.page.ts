import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api.config';
import { getApiErrorMessage } from '../../core/api-error';
import { CategoryResponse, CourseResponse, UserResponse } from '../../core/api.models';

type AdminSection = 'overview' | 'courses' | 'users' | 'categories';
type CourseStatus = CourseResponse['estado'] | 'TODOS';
type CourseEditor = {
  titulo: string;
  slug: string;
  descripcion: string;
  responsableId: string;
  precio: number | null;
  nivel: string;
  estado: CourseResponse['estado'];
  recomendadoPara: string;
  duracionEstimada: string;
  ruta: string;
  categoriaAcademica: string;
  categoriaIds: string[];
};
type CategoryEditor = { nombre: string; iconoUrl: string };
type UserEditor = { nombre: string; apellido: string; email: string; avatarUrl: string };

@Component({
  selector: 'app-admin-page',
  imports: [DatePipe, DecimalPipe, FormsModule],
  templateUrl: './admin.page.html',
  styleUrl: './admin.page.scss',
})
export class AdminPage implements OnInit {
  private readonly http = inject(HttpClient);

  readonly section = signal<AdminSection>('overview');
  readonly courses = signal<CourseResponse[]>([]);
  readonly users = signal<UserResponse[]>([]);
  readonly categories = signal<CategoryResponse[]>([]);
  readonly searchTerm = signal('');
  readonly courseStatus = signal<CourseStatus>('TODOS');
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly mutationError = signal('');
  readonly mutationSuccess = signal('');
  readonly saving = signal(false);
  readonly editorType = signal<'course' | 'category' | 'user' | null>(null);
  readonly editingId = signal<string | null>(null);
  readonly courseEditor = signal<CourseEditor>(this.emptyCourseEditor());
  readonly categoryEditor = signal<CategoryEditor>({ nombre: '', iconoUrl: '' });
  readonly userEditor = signal<UserEditor>({ nombre: '', apellido: '', email: '', avatarUrl: '' });

  readonly publishedCourses = computed(
    () => this.courses().filter((course) => course.estado === 'PUBLICADO').length,
  );
  readonly verifiedUsers = computed(() => this.users().filter((user) => user.emailVerificado).length);
  readonly visibleCourses = computed(() => {
    const term = this.searchTerm().trim().toLocaleLowerCase();
    const status = this.courseStatus();
    return this.courses().filter((course) => {
      const matchesTerm = !term || `${course.titulo} ${course.slug ?? ''}`.toLocaleLowerCase().includes(term);
      return matchesTerm && (status === 'TODOS' || course.estado === status);
    });
  });
  readonly visibleUsers = computed(() => {
    const term = this.searchTerm().trim().toLocaleLowerCase();
    return this.users().filter((user) =>
      `${user.nombre} ${user.apellido} ${user.email}`.toLocaleLowerCase().includes(term),
    );
  });
  readonly visibleCategories = computed(() => {
    const term = this.searchTerm().trim().toLocaleLowerCase();
    return this.categories().filter((category) => category.nombre.toLocaleLowerCase().includes(term));
  });

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.mutationError.set('');

    forkJoin({
      courses: this.http.get<CourseResponse[]>(`${API_BASE_URL}/cursos`),
      users: this.http.get<UserResponse[]>(`${API_BASE_URL}/usuarios`),
      categories: this.http.get<CategoryResponse[]>(`${API_BASE_URL}/categorias`),
    }).subscribe({
      next: ({ courses, users, categories }) => {
        this.courses.set(courses);
        this.users.set(users);
        this.categories.set(categories);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(
          error instanceof HttpErrorResponse && error.status === 403
            ? 'Tu cuenta no tiene permisos para consultar el panel administrador.'
            : getApiErrorMessage(error, 'No pudimos cargar la información del panel.'),
        );
      },
    });
  }

  selectSection(section: AdminSection): void {
    this.section.set(section);
    this.searchTerm.set('');
    this.courseStatus.set('TODOS');
    this.mutationError.set('');
    this.mutationSuccess.set('');
  }

  sectionTitle(): string {
    switch (this.section()) {
      case 'courses':
        return 'Cursos';
      case 'users':
        return 'Personas';
      case 'categories':
        return 'Categorías';
      default:
        return 'Resumen';
    }
  }

  statusLabel(status: CourseResponse['estado']): string {
    const labels: Record<CourseResponse['estado'], string> = {
      BORRADOR: 'Borrador',
      REVISION: 'En revisión',
      PUBLICADO: 'Publicado',
      ARCHIVADO: 'Archivado',
    };
    return labels[status];
  }

  openCourseEditor(course?: CourseResponse): void {
    const firstAdmin = this.users().find((user) => user.rol === 'ADMINISTRADOR');
    this.editingId.set(course?.id ?? null);
    this.courseEditor.set(course ? {
      titulo: course.titulo,
      slug: course.slug ?? '',
      descripcion: course.descripcion ?? '',
      responsableId: course.responsableId ?? '',
      precio: course.precio,
      nivel: course.nivel ?? '',
      estado: course.estado,
      recomendadoPara: course.recomendadoPara ?? '',
      duracionEstimada: course.duracionEstimada ?? '',
      ruta: course.ruta ?? '',
      categoriaAcademica: course.categoriaAcademica ?? '',
      categoriaIds: [...course.categoriaIds],
    } : {
      ...this.emptyCourseEditor(),
      responsableId: firstAdmin?.id ?? '',
    });
    this.editorType.set('course');
    this.mutationError.set('');
  }

  openCategoryEditor(category?: CategoryResponse): void {
    this.editingId.set(category?.id ?? null);
    this.categoryEditor.set({
      nombre: category?.nombre ?? '',
      iconoUrl: category?.iconoUrl ?? '',
    });
    this.editorType.set('category');
    this.mutationError.set('');
  }

  openUserEditor(user: UserResponse): void {
    this.editingId.set(user.id);
    this.userEditor.set({
      nombre: user.nombre,
      apellido: user.apellido,
      email: user.email,
      avatarUrl: user.avatarUrl ?? '',
    });
    this.editorType.set('user');
    this.mutationError.set('');
  }

  closeEditor(): void {
    if (this.saving()) {
      return;
    }
    this.editorType.set(null);
    this.editingId.set(null);
  }

  saveCourse(): void {
    const course = this.courseEditor();
    if (!course.titulo.trim() || !course.responsableId) {
      this.mutationError.set('El título y el responsable administrador son obligatorios.');
      return;
    }

    const request = {
      ...course,
      titulo: course.titulo.trim(),
      slug: course.slug.trim() || null,
      descripcion: course.descripcion.trim() || null,
      nivel: course.nivel.trim() || null,
      recomendadoPara: course.recomendadoPara.trim() || null,
      duracionEstimada: course.duracionEstimada.trim() || null,
      ruta: course.ruta.trim() || null,
      categoriaAcademica: course.categoriaAcademica.trim() || null,
    };
    const id = this.editingId();
    this.runMutation(
      id
        ? this.http.put<CourseResponse>(`${API_BASE_URL}/cursos/${id}`, request)
        : this.http.post<CourseResponse>(`${API_BASE_URL}/cursos`, request),
      id ? 'Curso actualizado.' : 'Curso creado.',
    );
  }

  saveCategory(): void {
    const category = this.categoryEditor();
    if (!category.nombre.trim()) {
      this.mutationError.set('El nombre de la categoría es obligatorio.');
      return;
    }

    const request = {
      nombre: category.nombre.trim(),
      iconoUrl: category.iconoUrl.trim() || null,
    };
    const id = this.editingId();
    this.runMutation(
      id
        ? this.http.put<CategoryResponse>(`${API_BASE_URL}/categorias/${id}`, request)
        : this.http.post<CategoryResponse>(`${API_BASE_URL}/categorias`, request),
      id ? 'Categoría actualizada.' : 'Categoría creada.',
    );
  }

  saveUser(): void {
    const user = this.userEditor();
    const id = this.editingId();
    if (!id || !user.nombre.trim() || !user.apellido.trim() || !user.email.trim()) {
      this.mutationError.set('Nombre, apellido y correo son obligatorios.');
      return;
    }
    this.runMutation(
      this.http.patch<UserResponse>(`${API_BASE_URL}/usuarios/${id}`, {
        nombre: user.nombre.trim(),
        apellido: user.apellido.trim(),
        email: user.email.trim(),
        avatarUrl: user.avatarUrl.trim() || null,
      }),
      'Persona actualizada.',
    );
  }

  changeCourseStatus(course: CourseResponse, estado: CourseResponse['estado']): void {
    if (estado === course.estado) {
      return;
    }
    this.runMutation(
      this.http.patch<CourseResponse>(`${API_BASE_URL}/cursos/${course.id}`, { estado }),
      'Estado del curso actualizado.',
    );
  }

  toggleCourseCategory(categoryId: string, checked: boolean): void {
    this.courseEditor.update((editor) => ({
      ...editor,
      categoriaIds: checked
        ? [...new Set([...editor.categoriaIds, categoryId])]
        : editor.categoriaIds.filter((id) => id !== categoryId),
    }));
  }

  changeUserRole(user: UserResponse, role: string): void {
    if (role !== 'USUARIO' && role !== 'ADMINISTRADOR') {
      this.mutationError.set('El rol seleccionado no es válido.');
      return;
    }
    const rol: UserResponse['rol'] = role;
    if (rol === user.rol) {
      return;
    }
    this.runMutation(
      this.http.patch<UserResponse>(`${API_BASE_URL}/usuarios/${user.id}/rol`, { rol }),
      'Rol actualizado.',
    );
  }

  toggleUserVerification(user: UserResponse): void {
    const emailVerificado = !user.emailVerificado;
    this.runMutation(
      this.http.patch<UserResponse>(`${API_BASE_URL}/usuarios/${user.id}/email-verificado`, { emailVerificado }),
      emailVerificado ? 'Correo marcado como verificado.' : 'Verificación de correo retirada.',
    );
  }

  deleteCourse(course: CourseResponse): void {
    if (!globalThis.confirm(`¿Eliminar el curso "${course.titulo}"? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.runMutation(
      this.http.delete<void>(`${API_BASE_URL}/cursos/${course.id}`),
      'Curso eliminado.',
    );
  }

  deleteCategory(category: CategoryResponse): void {
    if (!globalThis.confirm(`¿Eliminar la categoría "${category.nombre}"? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.runMutation(
      this.http.delete<void>(`${API_BASE_URL}/categorias/${category.id}`),
      'Categoría eliminada.',
    );
  }

  deleteUser(user: UserResponse): void {
    if (user.rol === 'ADMINISTRADOR') {
      this.mutationError.set('Las cuentas administradoras no se eliminan desde este panel para evitar perder el acceso.');
      return;
    }
    if (!globalThis.confirm(`¿Eliminar a ${user.nombre} ${user.apellido}? Esta acción no se puede deshacer.`)) {
      return;
    }

    this.runMutation(
      this.http.delete<void>(`${API_BASE_URL}/usuarios/${user.id}`),
      'Persona eliminada.',
    );
  }

  private emptyCourseEditor(): CourseEditor {
    return {
      titulo: '',
      slug: '',
      descripcion: '',
      responsableId: '',
      precio: 0,
      nivel: '',
      estado: 'BORRADOR',
      recomendadoPara: '',
      duracionEstimada: '',
      ruta: '',
      categoriaAcademica: '',
      categoriaIds: [],
    };
  }

  private runMutation<T>(request: Observable<T>, successMessage: string): void {
    if (this.saving()) {
      return;
    }
    this.saving.set(true);
    this.mutationError.set('');
    this.mutationSuccess.set('');
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.editorType.set(null);
        this.editingId.set(null);
        this.mutationSuccess.set(successMessage);
        this.loadDashboard();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.mutationError.set(getApiErrorMessage(error, 'No se pudo completar la operación.'));
      },
    });
  }
}
