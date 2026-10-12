import { DecimalPipe } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { API_BASE_URL } from '../../core/api.config';
import { getApiErrorMessage } from '../../core/api-error';
import { CategoryResponse, CourseResponse } from '../../core/api.models';

interface CatalogCourse extends CourseResponse {
  categoryNames: string[];
}

@Component({
  selector: 'app-catalog-page',
  imports: [DecimalPipe, FormsModule, RouterLink],
  templateUrl: './catalog.page.html',
  styleUrl: './catalog.page.scss',
})
export class CatalogPage implements OnInit {
  private readonly http = inject(HttpClient);

  readonly courses = signal<CatalogCourse[]>([]);
  readonly categories = signal<CategoryResponse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly searchTerm = signal('');
  readonly selectedCategory = signal('Todas');
  readonly selectedLevel = signal('Todos los niveles');

  readonly filteredCourses = computed(() => {
    const search = this.searchTerm().trim().toLocaleLowerCase();
    const category = this.selectedCategory();
    const level = this.selectedLevel();

    return this.courses().filter((course) => {
      const matchesSearch =
        !search ||
        `${course.titulo} ${course.descripcion ?? ''} ${course.categoryNames.join(' ')} ${course.nivel ?? ''}`
          .toLocaleLowerCase()
          .includes(search);
      const matchesCategory = category === 'Todas' || course.categoryNames.includes(category);
      const matchesLevel = level === 'Todos los niveles' || course.nivel === level;
      return matchesSearch && matchesCategory && matchesLevel;
    });
  });

  ngOnInit(): void {
    this.loadCatalog();
  }

  loadCatalog(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    const params = new HttpParams().set('estado', 'PUBLICADO');
    forkJoin({
      courses: this.http.get<CourseResponse[]>(`${API_BASE_URL}/cursos`, { params }),
      categories: this.http.get<CategoryResponse[]>(`${API_BASE_URL}/categorias`),
    }).subscribe({
      next: ({ courses, categories }) => {
        this.categories.set(categories);
        const categoryNames = new Map(categories.map((category) => [category.id, category.nombre]));
        this.courses.set(
          courses.map((course) => ({
            ...course,
            categoryNames: course.categoriaIds
              .map((categoryId) => categoryNames.get(categoryId))
              .filter((name): name is string => name !== undefined),
          })),
        );
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(
          error instanceof HttpErrorResponse && error.status === 401
            ? 'Inicia sesión para explorar los cursos disponibles.'
            : getApiErrorMessage(error, 'No pudimos cargar los cursos. Inténtalo de nuevo.'),
        );
      },
    });
  }

  setSearch(value: string): void {
    this.searchTerm.set(value);
  }
}
