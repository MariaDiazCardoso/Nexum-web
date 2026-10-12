import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorResponse } from './api.models';

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  const response = error.error as ApiErrorResponse | null;
  if (response?.message) {
    return response.message;
  }

  if (error.status === 0) {
    return 'No pudimos conectar con Nexum. Verifica que el servidor esté disponible e inténtalo de nuevo.';
  }

  return fallback;
}
