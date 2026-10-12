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
}

export interface CategoryResponse {
  id: string;
  nombre: string;
  iconoUrl: string | null;
}

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

export interface EnrollmentResponse {
  id: string;
  usuarioId: string;
  cursoId: string;
  estado: string;
  progresoPorcentaje: number;
  fechaInscripcion: string;
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

export interface ApiErrorResponse {
  status?: number;
  message?: string;
  errores?: Array<{ campo: string; mensaje: string }>;
}
