import { DatePipe } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiEndpointsService } from '../../core/api-endpoints.service';
import { API_BASE_URL } from '../../core/api.config';
import { getApiErrorMessage } from '../../core/api-error';
import {
  CertificadoResponse,
  CourseResponse,
  EnrollmentResponse,
} from '../../core/api.models';
import { AuthService } from '../../core/auth.service';

interface CompletedEnrollment {
  enrollment: EnrollmentResponse;
  course: CourseResponse;
}

@Component({
  selector: 'app-certificates-page',
  imports: [DatePipe, RouterLink],
  templateUrl: './certificates.page.html',
  styleUrl: './certificates.page.scss',
})
export class CertificatesPage implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(ApiEndpointsService);
  private readonly http = inject(HttpClient);

  readonly certificates = signal<CertificadoResponse[]>([]);
  readonly courses = signal<CourseResponse[]>([]);
  readonly enrollments = signal<EnrollmentResponse[]>([]);
  readonly loading = signal(true);
  readonly claiming = signal(false);
  readonly errorMessage = signal('');
  readonly selectedCert = signal<CertificadoResponse | null>(null);

  readonly coursesById = computed(() => {
    return new Map(this.courses().map((c) => [c.id, c]));
  });

  // Cursos con progreso >= 100% que aún no tienen certificado generado
  readonly claimableCourses = computed<CompletedEnrollment[]>(() => {
    const certCourseIds = new Set(this.certificates().map((c) => c.cursoId));
    const map = this.coursesById();

    return this.enrollments()
      .filter((e) => e.progresoPorcentaje >= 100 || e.estado === 'COMPLETADA')
      .filter((e) => !certCourseIds.has(e.cursoId))
      .flatMap((e) => {
        const course = map.get(e.cursoId);
        return course ? [{ enrollment: e, course }] : [];
      });
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.loading.set(false);
      this.errorMessage.set('Inicia sesión para ver tus certificados.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    forkJoin({
      certs: this.api.getCertificados(userId),
      enrollments: this.http.get<EnrollmentResponse[]>(`${API_BASE_URL}/inscripciones`, {
        params: new HttpParams().set('usuarioId', userId),
      }),
      courses: this.http.get<CourseResponse[]>(`${API_BASE_URL}/cursos`),
    }).subscribe({
      next: ({ certs, enrollments, courses }) => {
        this.certificates.set(certs);
        this.enrollments.set(enrollments);
        this.courses.set(courses);
        this.loading.set(false);
      },
      error: (err: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(getApiErrorMessage(err, 'No pudimos cargar tus certificados.'));
      },
    });
  }

  getCourseTitle(courseId: string): string {
    return this.coursesById().get(courseId)?.titulo ?? 'Curso Nexum';
  }

  claimCertificate(courseId: string): void {
    const userId = this.auth.userId();
    if (!userId) return;

    this.claiming.set(true);
    const code = `NEX-${Math.random().toString(36).substring(2, 8).toUpperCase()}-${new Date().getFullYear()}`;

    this.api
      .generarCertificado({
        usuarioId: userId,
        cursoId: courseId,
        codigoVerificacion: code,
      })
      .subscribe({
        next: (newCert) => {
          this.claiming.set(false);
          this.certificates.update((prev) => [newCert, ...prev]);
          this.openDiploma(newCert);
        },
        error: (err: unknown) => {
          this.claiming.set(false);
          this.errorMessage.set(getApiErrorMessage(err, 'No se pudo generar el certificado.'));
        },
      });
  }

  openDiploma(cert: CertificadoResponse): void {
    this.selectedCert.set(cert);
  }

  closeDiploma(): void {
    this.selectedCert.set(null);
  }

  printDiploma(): void {
    if (typeof window !== 'undefined') {
      window.print();
    }
  }
}
