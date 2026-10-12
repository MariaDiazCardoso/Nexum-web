import { DatePipe } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { API_BASE_URL } from '../../core/api.config';
import { getApiErrorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import {
  CourseResponse,
  EnrollmentResponse,
  LessonResponse,
  LessonProgressResponse,
  ModuleResponse,
} from '../../core/api.models';

interface ModuleDraft {
  titulo: string;
  orden: number;
  descripcion: string;
  slug: string;
}

interface LessonDraft {
  moduloId: string;
  titulo: string;
  tipo: string;
  orden: number;
  contenidoTexto: string;
  videoUrl: string;
  dificultad: string;
  duracionEstimada: string;
  xpOtorgada: number | null;
}

@Component({
  selector: 'app-course-detail-page',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './course-detail.page.html',
  styleUrl: './course-detail.page.scss',
})
export class CourseDetailPage implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  readonly auth = inject(AuthService);

  readonly course = signal<CourseResponse | null>(null);
  readonly modules = signal<ModuleResponse[]>([]);
  readonly lessons = signal<LessonResponse[]>([]);
  readonly enrollment = signal<EnrollmentResponse | null>(null);
  readonly lessonProgress = signal<LessonProgressResponse[]>([]);
  readonly selectedLesson = signal<LessonResponse | null>(null);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal('');
  readonly actionMessage = signal('');
  readonly actionError = signal(false);
  readonly moduleEditorOpen = signal(false);
  readonly lessonEditorModuleId = signal<string | null>(null);
  readonly moduleDraft = signal<ModuleDraft>({ titulo: '', orden: 1, descripcion: '', slug: '' });
  readonly lessonDraft = signal<LessonDraft>({
    moduloId: '',
    titulo: '',
    tipo: 'TEXTO',
    orden: 1,
    contenidoTexto: '',
    videoUrl: '',
    dificultad: '',
    duracionEstimada: '',
    xpOtorgada: 0,
  });

  readonly orderedModules = computed(() => [...this.modules()].sort((a, b) => a.orden - b.orden));
  readonly canReadLessons = computed(() => this.auth.isAdmin() || this.enrollment() !== null);

  ngOnInit(): void {
    this.loadCourse();
  }

  loadCourse(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    if (!courseId) {
      this.loading.set(false);
      this.errorMessage.set('No encontramos el identificador del curso.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');
    const params = new HttpParams().set('cursoId', courseId);
    const userId = this.auth.userId();
    const enrollments = this.auth.isAdmin() || !userId
      ? of([] as EnrollmentResponse[])
      : this.http.get<EnrollmentResponse[]>(`${API_BASE_URL}/inscripciones`, {
          params: new HttpParams().set('usuarioId', userId).set('cursoId', courseId),
        });

    forkJoin({
      course: this.http.get<CourseResponse>(`${API_BASE_URL}/cursos/${courseId}`),
      modules: this.http.get<ModuleResponse[]>(`${API_BASE_URL}/modulos`, { params }),
      lessons: this.http.get<LessonResponse[]>(`${API_BASE_URL}/lecciones`, { params }),
      enrollments,
      progress: this.auth.isAdmin() || !userId
        ? of([] as LessonProgressResponse[])
        : this.http.get<LessonProgressResponse[]>(`${API_BASE_URL}/progresos-leccion`, {
            params: new HttpParams().set('usuarioId', userId).set('cursoId', courseId),
          }),
    }).subscribe({
      next: ({ course, modules, lessons, enrollments: userEnrollments, progress }) => {
        if (!this.auth.isAdmin() && course.estado !== 'PUBLICADO') {
          this.loading.set(false);
          this.errorMessage.set('Este curso no está disponible en el catálogo.');
          return;
        }
        this.course.set(course);
        this.modules.set(modules);
        this.lessons.set(lessons);
        this.enrollment.set(userEnrollments[0] ?? null);
        this.lessonProgress.set(progress);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(getApiErrorMessage(error, 'No pudimos cargar la información del curso.'));
      },
    });
  }

  enroll(): void {
    const userId = this.auth.userId();
    const course = this.course();
    if (!userId || !course || this.saving()) {
      this.actionError.set(true);
      this.actionMessage.set('Cierra sesión e inicia sesión nuevamente para actualizar tu sesión antes de inscribirte.');
      return;
    }
    if (course.precio !== null && course.precio > 0) {
      this.actionError.set(true);
      this.actionMessage.set('La inscripción de cursos con costo estará disponible cuando se integre el pago en línea.');
      return;
    }

    this.saving.set(true);
    this.actionError.set(false);
    this.actionMessage.set('');
    this.http.post<EnrollmentResponse>(`${API_BASE_URL}/inscripciones`, {
      usuarioId: userId,
      cursoId: course.id,
    }).subscribe({
      next: (enrollment) => {
        this.enrollment.set(enrollment);
        this.saving.set(false);
        this.actionMessage.set('Ya estás inscrito. ¡Empecemos!');
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.actionError.set(true);
        this.actionMessage.set(
          error instanceof HttpErrorResponse && error.status === 409
            ? 'Ya tienes una inscripción en este curso. Actualiza la página para continuar.'
            : getApiErrorMessage(error, 'No se pudo completar la inscripción.'),
        );
        if (error instanceof HttpErrorResponse && error.status === 409) {
          this.loadCourse();
        }
      },
    });
  }

  openModuleEditor(): void {
    this.moduleDraft.set({
      titulo: '',
      orden: this.modules().reduce((highest, module) => Math.max(highest, module.orden), 0) + 1,
      descripcion: '',
      slug: '',
    });
    this.moduleEditorOpen.set(true);
    this.actionMessage.set('');
  }

  saveModule(): void {
    const course = this.course();
    const draft = this.moduleDraft();
    if (!course || !draft.titulo.trim()) {
      this.actionError.set(true);
      this.actionMessage.set('Escribe el título del módulo.');
      return;
    }

    this.saveContent(`${API_BASE_URL}/modulos`, {
      cursoId: course.id,
      titulo: draft.titulo.trim(),
      orden: draft.orden,
      descripcion: draft.descripcion.trim() || null,
      slug: draft.slug.trim() || null,
    }, 'Módulo creado.', () => this.moduleEditorOpen.set(false));
  }

  openLessonEditor(module: ModuleResponse): void {
    const existingLessons = this.lessons().filter((lesson) => lesson.moduloId === module.id);
    this.lessonDraft.set({
      moduloId: module.id,
      titulo: '',
      tipo: 'TEXTO',
      orden: existingLessons.reduce((highest, lesson) => Math.max(highest, lesson.orden), 0) + 1,
      contenidoTexto: '',
      videoUrl: '',
      dificultad: '',
      duracionEstimada: '',
      xpOtorgada: 0,
    });
    this.lessonEditorModuleId.set(module.id);
    this.actionMessage.set('');
  }

  saveLesson(): void {
    const draft = this.lessonDraft();
    if (!draft.titulo.trim()) {
      this.actionError.set(true);
      this.actionMessage.set('Escribe el título de la lección.');
      return;
    }

    this.saveContent(`${API_BASE_URL}/lecciones`, {
      moduloId: draft.moduloId,
      titulo: draft.titulo.trim(),
      tipo: draft.tipo.trim() || null,
      orden: draft.orden,
      contenidoTexto: draft.contenidoTexto.trim() || null,
      videoUrl: draft.videoUrl.trim() || null,
      dificultad: draft.dificultad.trim() || null,
      duracionEstimada: draft.duracionEstimada.trim() || null,
      xpOtorgada: draft.xpOtorgada,
    }, 'Lección creada.', () => this.lessonEditorModuleId.set(null));
  }

  selectLesson(lesson: LessonResponse): void {
    if (this.canReadLessons()) {
      this.selectedLesson.set(lesson);
    }
  }

  isLessonComplete(lessonId: string): boolean {
    return this.lessonProgress().some((progress) => progress.leccionId === lessonId && progress.completado);
  }

  completeLesson(lesson: LessonResponse): void {
    const userId = this.auth.userId();
    if (!userId || !this.enrollment() || this.saving() || this.isLessonComplete(lesson.id)) {
      return;
    }

    const existing = this.lessonProgress().find((progress) => progress.leccionId === lesson.id);
    this.saving.set(true);
    this.actionError.set(false);
    this.actionMessage.set('');
    const request = existing
      ? this.http.patch<LessonProgressResponse>(`${API_BASE_URL}/progresos-leccion/${existing.id}`, { completado: true })
      : this.http.post<LessonProgressResponse>(`${API_BASE_URL}/progresos-leccion`, {
          usuarioId,
          leccionId: lesson.id,
          completado: true,
        });

    request.subscribe({
      next: (progress) => {
        this.lessonProgress.update((items) => [
          ...items.filter((item) => item.id !== progress.id && item.leccionId !== progress.leccionId),
          progress,
        ]);
        this.saving.set(false);
        this.actionMessage.set('Lección completada. ¡Buen trabajo!');
        this.loadCourse();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.actionError.set(true);
        this.actionMessage.set(getApiErrorMessage(error, 'No se pudo guardar el progreso de la lección.'));
      },
    });
  }

  private saveContent<T>(url: string, body: object, success: string, afterSave: () => void): void {
    if (this.saving()) {
      return;
    }
    this.saving.set(true);
    this.actionError.set(false);
    this.actionMessage.set('');
    this.http.post<T>(url, body).subscribe({
      next: () => {
        this.saving.set(false);
        afterSave();
        this.actionMessage.set(success);
        this.loadCourse();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.actionError.set(true);
        this.actionMessage.set(getApiErrorMessage(error, 'No se pudo guardar el contenido.'));
      },
    });
  }
}
