import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import {
  CalificacionRequest,
  CalificacionResponse,
  CertificadoRequest,
  CertificadoResponse,
  CodigoVerificacionRequest,
  CodigoVerificacionResponse,
  CodigoVerificacionValidacionRequest,
  ComentarioLeccionRequest,
  ComentarioLeccionResponse,
  ExamenResponse,
  MedallaResponse,
  PreguntaResponse,
  UserResponse,
  UserUpdateRequest,
  UsuarioMedallaResponse,
} from './api.models';

@Injectable({ providedIn: 'root' })
export class ApiEndpointsService {
  private readonly http = inject(HttpClient);

  // ── Usuarios ──────────────────────────────────────────────
  getUser(userId: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${API_BASE_URL}/usuarios/${userId}`);
  }

  updateUser(userId: string, data: UserUpdateRequest): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${API_BASE_URL}/usuarios/${userId}`, data);
  }

  // ── Verificación de Correo ───────────────────────────────
  generarCodigo(request: CodigoVerificacionRequest): Observable<CodigoVerificacionResponse> {
    return this.http.post<CodigoVerificacionResponse>(`${API_BASE_URL}/codigos-verificacion`, request);
  }

  validarCodigo(codigoId: string, request: CodigoVerificacionValidacionRequest): Observable<CodigoVerificacionResponse> {
    return this.http.post<CodigoVerificacionResponse>(`${API_BASE_URL}/codigos-verificacion/${codigoId}/validacion`, request);
  }

  // ── Certificados ─────────────────────────────────────────
  getCertificados(usuarioId?: string, codigo?: string): Observable<CertificadoResponse[]> {
    let params = new HttpParams();
    if (usuarioId) params = params.set('usuarioId', usuarioId);
    if (codigo) params = params.set('codigo', codigo);
    return this.http.get<CertificadoResponse[]>(`${API_BASE_URL}/certificados`, { params });
  }

  generarCertificado(request: CertificadoRequest): Observable<CertificadoResponse> {
    return this.http.post<CertificadoResponse>(`${API_BASE_URL}/certificados`, request);
  }

  // ── Medallas ─────────────────────────────────────────────
  getMedallas(): Observable<MedallaResponse[]> {
    return this.http.get<MedallaResponse[]>(`${API_BASE_URL}/medallas`);
  }

  getUserMedallas(usuarioId: string): Observable<UsuarioMedallaResponse[]> {
    return this.http.get<UsuarioMedallaResponse[]>(`${API_BASE_URL}/usuarios/${usuarioId}/medallas`);
  }

  // ── Evaluaciones (Exámenes) y Preguntas ───────────────────
  getExamenes(leccionId?: string): Observable<ExamenResponse[]> {
    let params = new HttpParams();
    if (leccionId) params = params.set('leccionId', leccionId);
    return this.http.get<ExamenResponse[]>(`${API_BASE_URL}/examenes`, { params });
  }

  getPreguntas(examenId?: string): Observable<PreguntaResponse[]> {
    let params = new HttpParams();
    if (examenId) params = params.set('examenId', examenId);
    return this.http.get<PreguntaResponse[]>(`${API_BASE_URL}/preguntas`, { params });
  }

  // ── Calificaciones ───────────────────────────────────────
  getCalificaciones(usuarioId?: string, cursoId?: string): Observable<CalificacionResponse[]> {
    let params = new HttpParams();
    if (usuarioId) params = params.set('usuarioId', usuarioId);
    if (cursoId) params = params.set('cursoId', cursoId);
    return this.http.get<CalificacionResponse[]>(`${API_BASE_URL}/calificaciones`, { params });
  }

  registrarCalificacion(request: CalificacionRequest): Observable<CalificacionResponse> {
    return this.http.post<CalificacionResponse>(`${API_BASE_URL}/calificaciones`, request);
  }

  // ── Comentarios de Lección ───────────────────────────────
  getComentarios(leccionId: string): Observable<ComentarioLeccionResponse[]> {
    const params = new HttpParams().set('leccionId', leccionId);
    return this.http.get<ComentarioLeccionResponse[]>(`${API_BASE_URL}/comentarios-leccion`, { params });
  }

  crearComentario(request: ComentarioLeccionRequest): Observable<ComentarioLeccionResponse> {
    return this.http.post<ComentarioLeccionResponse>(`${API_BASE_URL}/comentarios-leccion`, request);
  }
}
