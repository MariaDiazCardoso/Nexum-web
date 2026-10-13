import { Component, computed, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiEndpointsService } from '../../../core/api-endpoints.service';
import { ExamenResponse, PreguntaResponse } from '../../../core/api.models';
import { AuthService } from '../../../core/auth.service';

@Component({
  selector: 'app-quiz-dialog',
  imports: [FormsModule],
  template: `
    <div class="quiz-modal-backdrop" (click)="close.emit()">
      <div class="quiz-modal-card" (click)="$event.stopPropagation()">

        <header class="quiz-header">
          <span class="quiz-kicker">EVALUACIÓN DE LECCIÓN</span>
          <h2>{{ exam.titulo || 'Quiz de Evaluación' }}</h2>
        </header>

        @if (loading()) {
          <div style="text-align: center; padding: 32px;">
            <span class="spinner"></span>
            <p style="margin-top: 12px; color: var(--text-secondary);">Cargando preguntas...</p>
          </div>
        } @else if (questions().length === 0) {
          <div style="text-align: center; padding: 24px;">
            <p style="color: var(--text-secondary);">Esta lección no tiene preguntas registradas aún.</p>
            <div class="quiz-actions">
              <button class="btn btn-secondary" type="button" (click)="close.emit()">Cerrar</button>
            </div>
          </div>
        } @else if (resultSubmitted()) {

          <!-- Vista de Resultados -->
          <div class="quiz-result-box" [class.passed]="isPassed()" [class.failed]="!isPassed()">
            <span style="font-size: 40px;" aria-hidden="true">{{ isPassed() ? '🎉' : '📚' }}</span>
            <div class="result-score">{{ finalScore() }}%</div>
            <h3>{{ isPassed() ? '¡Felicitaciones! Has aprobado' : 'No has alcanzado la nota mínima' }}</h3>
            <p style="margin-top: 6px; font-size: 13px;">
              {{ isPassed() ? 'Has demostrado dominio en este tema y sumado XP a tu perfil.' : 'Revisa el contenido de la lección e inténtalo nuevamente.' }}
            </p>
          </div>

          <div class="quiz-actions">
            @if (!isPassed()) {
              <button class="btn btn-secondary" type="button" (click)="retry()">Reintentar</button>
            }
            <button class="btn btn-primary" type="button" (click)="close.emit()">Continuar</button>
          </div>

        } @else {

          <!-- Pregunta Actual -->
          @if (currentQuestion(); as q) {
            <div class="question-block">
              <span class="badge badge-blue" style="margin-bottom: 8px;">
                Pregunta {{ currentQuestionIndex() + 1 }} de {{ questions().length }}
              </span>
              <h3>{{ q.textoPregunta }}</h3>

              <div class="options-list">
                @for (opt of q.opciones; track opt.id) {
                  <label class="option-item" [class.selected]="selectedAnswers()[q.id] === opt.id">
                    <input
                      type="radio"
                      [name]="q.id"
                      [value]="opt.id"
                      [checked]="selectedAnswers()[q.id] === opt.id"
                      (change)="selectOption(q.id, opt.id)"
                    />
                    <span>{{ opt.textoOpcion }}</span>
                  </label>
                }
              </div>
            </div>

            <div class="quiz-actions">
              <button class="btn btn-ghost" type="button" (click)="close.emit()">Cancelar</button>

              @if (currentQuestionIndex() > 0) {
                <button class="btn btn-secondary" type="button" (click)="prevQuestion()">Anterior</button>
              }

              @if (currentQuestionIndex() < questions().length - 1) {
                <button class="btn btn-primary" type="button" [disabled]="!selectedAnswers()[q.id]" (click)="nextQuestion()">
                  Siguiente
                </button>
              } @else {
                <button class="btn btn-primary" type="button" [disabled]="!selectedAnswers()[q.id] || submitting()" (click)="submitQuiz()">
                  {{ submitting() ? 'Evaluando...' : 'Enviar Respuestas' }}
                </button>
              }
            </div>
          }

        }

      </div>
    </div>
  `,
  styleUrl: './quiz-dialog.component.scss',
})
export class QuizDialogComponent implements OnInit {
  @Input({ required: true }) exam!: ExamenResponse;
  @Input({ required: true }) courseId!: string;
  @Input() lessonId?: string;
  @Output() readonly close = new EventEmitter<void>();
  @Output() readonly completed = new EventEmitter<{ passed: boolean; score: number }>();

  private readonly api = inject(ApiEndpointsService);
  private readonly auth = inject(AuthService);

  readonly questions = signal<PreguntaResponse[]>([]);
  readonly currentQuestionIndex = signal(0);
  readonly selectedAnswers = signal<Record<string, string>>({});
  readonly loading = signal(true);
  readonly submitting = signal(false);

  // Resultado
  readonly resultSubmitted = signal(false);
  readonly isPassed = signal(false);
  readonly finalScore = signal(0);

  readonly currentQuestion = computed<PreguntaResponse | undefined>(() => {
    const qs = this.questions();
    return qs[this.currentQuestionIndex()];
  });

  ngOnInit(): void {
    this.loadQuestions();
  }

  loadQuestions(): void {
    if (!this.exam?.id) return;
    this.loading.set(true);

    this.api.getPreguntas(this.exam.id).subscribe({
      next: (qs) => {
        this.questions.set(qs);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  selectOption(questionId: string, optionId: string): void {
    this.selectedAnswers.update((prev) => ({ ...prev, [questionId]: optionId }));
  }

  nextQuestion(): void {
    if (this.currentQuestionIndex() < this.questions().length - 1) {
      this.currentQuestionIndex.update((i) => i + 1);
    }
  }

  prevQuestion(): void {
    if (this.currentQuestionIndex() > 0) {
      this.currentQuestionIndex.update((i) => i - 1);
    }
  }

  retry(): void {
    this.selectedAnswers.set({});
    this.currentQuestionIndex.set(0);
    this.resultSubmitted.set(false);
  }

  submitQuiz(): void {
    this.submitting.set(true);
    const qs = this.questions();
    const answers = this.selectedAnswers();

    let correctCount = 0;
    for (const q of qs) {
      const selectedOptId = answers[q.id];
      const correctOpt = q.opciones.find((opt) => opt.esCorrecta);
      if (correctOpt && correctOpt.id === selectedOptId) {
        correctCount++;
      } else if (!correctOpt && selectedOptId) {
        // En caso de que el backend no envíe esCorrecta para alumnos, asumimos respuesta tentativa válida
        correctCount++;
      }
    }

    const percentage = qs.length > 0 ? Math.round((correctCount / qs.length) * 100) : 100;
    const minPass = this.exam.puntajeMinimoAprobacion ?? 70;
    const passed = percentage >= minPass;

    // Registrar en backend calificaciones
    const userId = this.auth.userId();
    if (userId) {
      this.api
        .registrarCalificacion({
          usuarioId: userId,
          cursoId: this.courseId,
          leccionId: this.lessonId,
          tipo: 'EXAMEN',
          titulo: this.exam.titulo,
          nota: percentage,
          notaMaxima: 100,
          peso: 1,
          comentario: passed ? 'Aprobado con éxito' : 'Intento registrado',
        })
        .subscribe({
          next: () => {
            this.finalizeResult(passed, percentage);
          },
          error: () => {
            // Mostrar resultado al usuario aun si falla el registro de la nota
            this.finalizeResult(passed, percentage);
          },
        });
    } else {
      this.finalizeResult(passed, percentage);
    }
  }

  private finalizeResult(passed: boolean, percentage: number): void {
    this.submitting.set(false);
    this.finalScore.set(percentage);
    this.isPassed.set(passed);
    this.resultSubmitted.set(true);
    this.completed.emit({ passed, score: percentage });
  }
}
