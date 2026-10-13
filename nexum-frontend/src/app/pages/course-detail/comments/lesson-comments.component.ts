import { DatePipe } from '@angular/common';
import { Component, inject, Input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiEndpointsService } from '../../../core/api-endpoints.service';
import { ComentarioLeccionResponse } from '../../../core/api.models';
import { AuthService } from '../../../core/auth.service';

@Component({
  selector: 'app-lesson-comments',
  imports: [DatePipe, FormsModule],
  template: `
    <section class="comments-section" style="margin-top: 32px; border-top: 1px solid var(--border); padding-top: 24px;">
      <h3 style="font-size: 1.1rem; font-weight: 800; margin-bottom: 6px; color: var(--text-primary);">
        Comentarios y Preguntas ({{ comments().length }})
      </h3>
      <p style="color: var(--text-secondary); font-size: 13px; margin-bottom: 20px;">
        Comparte tus dudas o comentarios con los instructores y otros alumnos.
      </p>

      <!-- Formulario para nuevo comentario -->
      <form (ngSubmit)="sendComment()" style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 24px;">
        <textarea
          class="input-field"
          style="min-height: 80px; resize: vertical;"
          placeholder="Escribe tu pregunta o aporte sobre esta lección..."
          [ngModel]="newCommentText()"
          (ngModelChange)="newCommentText.set($event)"
          name="commentText"
          required
        ></textarea>
        <div style="display: flex; justify-content: flex-end;">
          <button class="btn btn-primary btn-sm" type="submit" [disabled]="sending() || !newCommentText().trim()">
            {{ sending() ? 'Publicando...' : 'Publicar Comentario' }}
          </button>
        </div>
      </form>

      <!-- Lista de Comentarios -->
      @if (loading()) {
        <div style="text-align: center; padding: 20px;">
          <span class="spinner"></span>
        </div>
      } @else if (comments().length === 0) {
        <div style="background: var(--bg-surface); border: 1px dashed var(--border); border-radius: var(--radius-md); padding: 20px; text-align: center;">
          <p style="color: var(--text-muted); font-size: 13px; margin: 0;">
            Aún no hay comentarios en esta lección. ¡Sé el primero en participar!
          </p>
        </div>
      } @else {
        <div style="display: flex; flex-direction: column; gap: 14px;">
          @for (c of comments(); track c.id) {
            <article class="comment-bubble" style="background: var(--bg-surface); border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 16px;">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <div style="background: var(--gradient-brand); border-radius: 50%; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 12px; font-weight: 700;">
                    {{ c.autorNombre ? c.autorNombre.charAt(0).toUpperCase() : 'U' }}
                  </div>
                  <strong style="font-size: 13px; color: var(--text-primary);">{{ c.autorNombre || 'Estudiante Nexum' }}</strong>
                </div>
                <time style="font-size: 11px; color: var(--text-muted);">{{ c.fecha | date:'mediumDate' }}</time>
              </div>
              <p style="color: var(--text-secondary); font-size: 13px; line-height: 1.5; margin: 0; white-space: pre-line;">
                {{ c.contenido }}
              </p>
            </article>
          }
        </div>
      }
    </section>
  `,
})
export class LessonCommentsComponent implements OnInit, OnChanges {
  @Input({ required: true }) lessonId!: string;

  protected readonly auth = inject(AuthService);
  private readonly api = inject(ApiEndpointsService);

  readonly comments = signal<ComentarioLeccionResponse[]>([]);
  readonly loading = signal(false);
  readonly sending = signal(false);
  readonly newCommentText = signal('');

  ngOnInit(): void {
    this.loadComments();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['lessonId'] && !changes['lessonId'].isFirstChange()) {
      this.loadComments();
    }
  }

  loadComments(): void {
    if (!this.lessonId) return;
    this.loading.set(true);

    this.api.getComentarios(this.lessonId).subscribe({
      next: (list) => {
        this.comments.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  sendComment(): void {
    const text = this.newCommentText().trim();
    const userId = this.auth.userId();
    if (!text || !userId || !this.lessonId) return;

    this.sending.set(true);
    this.api
      .crearComentario({
        usuarioId: userId,
        leccionId: this.lessonId,
        contenido: text,
      })
      .subscribe({
        next: (created) => {
          this.sending.set(false);
          this.newCommentText.set('');
          this.comments.update((prev) => [created, ...prev]);
        },
        error: () => {
          this.sending.set(false);
        },
      });
  }
}
