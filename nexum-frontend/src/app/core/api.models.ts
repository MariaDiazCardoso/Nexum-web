/* ============================================================
   NEXUM – API Response/Request Models
   Synced with Spring Boot backend entities
   ============================================================ */

/* ── Autenticación y Usuario ────────────────────────────── */
export interface UserResponse {
  id: string;
  nombre: string;
  apellido: string;
  email: string;
  rol: 'USUARIO' | 'ADMINISTRADOR';
  puntosXp: number;
  avatarUrl: string | null;
  fechaRegistro: string;
  ultimaConexion: string | null;
  emailVerificado: boolean;
}

export interface UserUpdateRequest {
  nombre?: string;
  apellido?: string;
  avatarUrl?: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  expiraEn: number;
  usuario: UserResponse;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
}

/* ── Cursos y Categorías ────────────────────────────────── */
export interface CourseResponse {
  id: string;
  titulo: string;
  slug: string | null;
  descripcion: string | null;
  responsableId: string;
  precio: number | null;
  nivel: string | null;
  estado: 'BORRADOR' | 'REVISION' | 'PUBLICADO' | 'ARCHIVADO';
  promedioValoracion: number | null;
  fechaCreacion: string;
  recomendadoPara: string | null;
  duracionEstimada: string | null;
  ruta: string | null;
  categoriaAcademica: string | null;
  categoriaIds: string[];
  imagenUrl?: string | null;
}

export interface CategoryResponse {
  id: string;
  nombre: string;
  iconoUrl: string | null;
}

/* ── Módulos y Lecciones ────────────────────────────────── */
export interface ModuleResponse {
  id: string;
  cursoId: string;
  titulo: string;
  orden: number;
  descripcion: string | null;
  slug: string | null;
}

export interface LessonResponse {
  id: string;
  moduloId: string;
  titulo: string;
  tipo: string | null;
  orden: number;
  contenidoTexto: string | null;
  puntosOtorgados: number | null;
  videoUrl: string | null;
  xpOtorgada: number | null;
  slug: string | null;
  retoPractico: string | null;
  dificultad: string | null;
  duracionEstimada: string | null;
  duracionMinima: number | null;
  imagenUrl: string | null;
}

/* ── Inscripciones y Progreso ───────────────────────────── */
export interface EnrollmentResponse {
  id: string;
  usuarioId: string;
  cursoId: string;
  estado: 'ACTIVA' | 'COMPLETADA' | 'CANCELADA' | string;
  progresoPorcentaje: number;
  fechaInscripcion: string;
  fechaCompletado?: string | null;
}

export interface LessonProgressResponse {
  id: string;
  usuarioId: string;
  leccionId: string;
  completado: boolean;
  ultimoSegundoVisto: number | null;
  fechaCompletado: string | null;
  tiempoActivo: number | null;
}

/* ── Evaluaciones y Quizzes ─────────────────────────────── */
export interface ExamenResponse {
  id: string;
  leccionId: string;
  titulo: string;
  intentosMaximos: number;
  puntajeMinimoAprobacion: number;
}

export interface OpcionRespuestaResponse {
  id: string;
  textoOpcion: string;
  esCorrecta?: boolean | null;
}

export interface PreguntaResponse {
  id: string;
  examenId: string;
  textoPregunta: string;
  explicacionRespuesta?: string | null;
  opciones: OpcionRespuestaResponse[];
}

export interface CalificacionRequest {
  usuarioId: string;
  cursoId: string;
  leccionId?: string | null;
  tipo: string;
  titulo: string;
  nota: number;
  notaMaxima: number;
  peso?: number | null;
  comentario?: string | null;
}

export interface CalificacionResponse {
  id: string;
  usuarioId: string;
  cursoId: string;
  leccionId?: string | null;
  tipo: string;
  titulo: string;
  nota: number;
  notaMaxima: number;
  peso?: number | null;
  comentario?: string | null;
  fechaRegistro: string;
}

/* ── Certificados ───────────────────────────────────────── */
export interface CertificadoRequest {
  usuarioId: string;
  cursoId: string;
  codigoVerificacion?: string;
  urlCertificado?: string;
}

export interface CertificadoResponse {
  id: string;
  usuarioId: string;
  cursoId: string;
  codigoVerificacion: string;
  urlCertificado?: string | null;
  fechaEmision: string;
  nombreCurso?: string;
  nombreUsuario?: string;
}

/* ── Medallas / Gamificación ────────────────────────────── */
export interface MedallaResponse {
  id: string;
  nombre: string;
  descripcion: string | null;
  iconoUrl: string | null;
  criterio: string | null;
  puntosBono: number | null;
}

export interface UsuarioMedallaResponse {
  id: string;
  usuarioId: string;
  medallaId: string;
  fechaObtenida: string;
  medalla?: MedallaResponse;
}

/* ── Comentarios de Lección ─────────────────────────────── */
export interface ComentarioLeccionRequest {
  usuarioId: string;
  leccionId: string;
  comentarioPadreId?: string | null;
  contenido: string;
}

export interface ComentarioLeccionResponse {
  id: string;
  usuarioId: string;
  leccionId: string;
  comentarioPadreId?: string | null;
  contenido: string;
  fecha: string;
  autorNombre?: string;
}

/* ── Código de Verificación ─────────────────────────────── */
export interface CodigoVerificacionRequest {
  usuarioId: string;
  tipo: string;
  minutosVigencia: number;
}

export interface CodigoVerificacionResponse {
  id: string;
  usuarioId: string;
  tipo: string;
  usado: boolean;
  expiraEn: string;
  fechaCreacion: string;
  codigo?: string | null;
}

export interface CodigoVerificacionValidacionRequest {
  codigo: string;
}

/* ── Errores de API ─────────────────────────────────────── */
export interface ApiErrorResponse {
  status?: number;
  message?: string;
  errores?: Array<{ campo: string; mensaje: string }>;
}
