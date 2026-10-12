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

export interface ApiErrorResponse {
  status?: number;
  message?: string;
  errores?: Array<{ campo: string; mensaje: string }>;
}
