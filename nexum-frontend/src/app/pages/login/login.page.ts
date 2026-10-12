import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { getApiErrorMessage } from '../../core/api-error';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.page.html',
  styleUrl: './login.page.scss',
})
export class LoginPage {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly submitting = signal(false);
  readonly errorMessage = signal('');
  readonly showPassword = signal(false);
  readonly registered = this.activatedRoute.snapshot.queryParamMap.get('registered') === 'true';

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: ['', Validators.required],
  });

  submit(): void {
    this.errorMessage.set('');
    this.form.markAllAsTouched();
    if (this.form.invalid || this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.authService.login(this.form.getRawValue()).subscribe({
      next: (response) => {
        this.submitting.set(false);
        void this.router.navigateByUrl(response.usuario.rol === 'ADMINISTRADOR' ? '/admin' : '/catalog');
      },
      error: (error: unknown) => {
        this.submitting.set(false);
        this.errorMessage.set(
          error instanceof HttpErrorResponse && error.status === 401
            ? 'El correo o la contraseña no son correctos.'
            : getApiErrorMessage(error, 'No se pudo iniciar sesión. Inténtalo de nuevo.'),
        );
      },
    });
  }
}
