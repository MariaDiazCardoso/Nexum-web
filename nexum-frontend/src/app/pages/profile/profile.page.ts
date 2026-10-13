import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApiEndpointsService } from '../../core/api-endpoints.service';
import { getApiErrorMessage } from '../../core/api-error';
import { UserResponse, UsuarioMedallaResponse } from '../../core/api.models';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-profile-page',
  imports: [FormsModule],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.scss',
})
export class ProfilePage implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(ApiEndpointsService);

  readonly user = signal<UserResponse | null>(null);
  readonly userMedals = signal<UsuarioMedallaResponse[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly requestingCode = signal(false);
  readonly validatingCode = signal(false);

  // Edición
  readonly editNombre = signal('');
  readonly editApellido = signal('');

  // Verificación de código
  readonly generatedCodeId = signal<string | null>(null);
  readonly lastGeneratedCode = signal<string | null>(null);
  readonly enteredCode = signal('');

  // Feedback
  readonly feedbackMessage = signal('');
  readonly isFeedbackError = signal(false);

  // Computeds
  readonly userInitial = computed(() => {
    const name = this.user()?.nombre ?? 'U';
    return name.charAt(0).toUpperCase();
  });

  readonly currentLevel = computed(() => {
    const xp = this.user()?.puntosXp ?? 0;
    return Math.floor(xp / 100) + 1;
  });

  readonly levelProgress = computed(() => {
    const xp = this.user()?.puntosXp ?? 0;
    return xp % 100;
  });

  readonly xpToNextLevel = computed(() => {
    return 100 - this.levelProgress();
  });

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.loading.set(false);
      this.feedbackMessage.set('No se encontró sesión activa.');
      this.isFeedbackError.set(true);
      return;
    }

    this.loading.set(true);
    forkJoin({
      userData: this.api.getUser(userId),
      medals: this.api.getUserMedallas(userId),
    }).subscribe({
      next: ({ userData, medals }) => {
        this.user.set(userData);
        this.editNombre.set(userData.nombre);
        this.editApellido.set(userData.apellido);
        this.userMedals.set(medals);
        this.loading.set(false);
      },
      error: (err: unknown) => {
        this.loading.set(false);
        this.feedbackMessage.set(getApiErrorMessage(err, 'No se pudo cargar la información del usuario.'));
        this.isFeedbackError.set(true);
      },
    });
  }

  saveProfile(): void {
    const userId = this.auth.userId();
    if (!userId) return;

    this.saving.set(true);
    this.feedbackMessage.set('');

    this.api
      .updateUser(userId, {
        nombre: this.editNombre().trim(),
        apellido: this.editApellido().trim(),
      })
      .subscribe({
        next: (updated) => {
          this.user.set(updated);
          this.saving.set(false);
          this.feedbackMessage.set('Perfil actualizado exitosamente.');
          this.isFeedbackError.set(false);
        },
        error: (err: unknown) => {
          this.saving.set(false);
          this.feedbackMessage.set(getApiErrorMessage(err, 'No se pudo actualizar el perfil.'));
          this.isFeedbackError.set(true);
        },
      });
  }

  requestVerificationCode(): void {
    const userId = this.auth.userId();
    if (!userId) return;

    this.requestingCode.set(true);
    this.feedbackMessage.set('');

    this.api
      .generarCodigo({
        usuarioId: userId,
        tipo: 'EMAIL',
        minutosVigencia: 15,
      })
      .subscribe({
        next: (resp) => {
          this.requestingCode.set(false);
          this.generatedCodeId.set(resp.id);
          this.lastGeneratedCode.set(resp.codigo ?? null);
          this.feedbackMessage.set('Código de verificación generado.');
          this.isFeedbackError.set(false);
        },
        error: (err: unknown) => {
          this.requestingCode.set(false);
          this.feedbackMessage.set(getApiErrorMessage(err, 'No se pudo generar el código.'));
          this.isFeedbackError.set(true);
        },
      });
  }

  validateVerificationCode(): void {
    const codeId = this.generatedCodeId();
    const code = this.enteredCode().trim();
    if (!codeId || !code) return;

    this.validatingCode.set(true);
    this.feedbackMessage.set('');

    this.api.validarCodigo(codeId, { codigo: code }).subscribe({
      next: () => {
        this.validatingCode.set(false);
        this.generatedCodeId.set(null);
        this.lastGeneratedCode.set(null);
        this.enteredCode.set('');
        this.feedbackMessage.set('¡Tu correo ha sido verificado con éxito!');
        this.isFeedbackError.set(false);
        this.loadProfile();
      },
      error: (err: unknown) => {
        this.validatingCode.set(false);
        this.feedbackMessage.set(getApiErrorMessage(err, 'El código ingresado es incorrecto o ha expirado.'));
        this.isFeedbackError.set(true);
      },
    });
  }
}
