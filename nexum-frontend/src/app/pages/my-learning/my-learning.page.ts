import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { API_BASE_URL } from '../../core/api.config';
import { getApiErrorMessage } from '../../core/api-error';
import { AuthService } from '../../core/auth.service';
import { CourseResponse, EnrollmentResponse } from '../../core/api.models';

interface LearningCourse {
  enrollment: EnrollmentResponse;
  course: CourseResponse;
}

@Component({
  selector: 'app-my-learning-page',
  imports: [DatePipe, DecimalPipe, RouterLink],
  templateUrl: './my-learning.page.html',
  styleUrl: './my-learning.page.scss',
})
export class MyLearningPage implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  readonly learningCourses = signal<LearningCourse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly activeCourses = computed(
    () => this.learningCourses().filter(({ enrollment }) => enrollment.estado === 'ACTIVA').length,
  );

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.loading.set(false);
      this.errorMessage.set('Tu sesión necesita actualizarse. Cierra sesión e inicia sesión nuevamente.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');
    forkJoin({
      enrollments: this.http.get<EnrollmentResponse[]>(`${API_BASE_URL}/inscripciones`, {
        params: new HttpParams().set('usuarioId', userId),
      }),
      courses: this.http.get<CourseResponse[]>(`${API_BASE_URL}/cursos`),
    }).subscribe({
      next: ({ enrollments, courses }) => {
        const coursesById = new Map(courses.map((course) => [course.id, course]));
        this.learningCourses.set(
          enrollments.flatMap((enrollment) => {
            const course = coursesById.get(enrollment.cursoId);
            return course ? [{ enrollment, course }] : [];
          }),
        );
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(getApiErrorMessage(error, 'No pudimos cargar tus cursos.'));
      },
    });
  }
}
