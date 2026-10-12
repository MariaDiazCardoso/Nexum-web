# Nexum

Plataforma de cursos en línea. Monorepo con dos partes:

| Carpeta | Qué es | Tecnología |
|---|---|---|
| `/` (raíz) | Backend REST | Java 21, Spring Boot 4.1.1, Maven, PostgreSQL, Spring Security + JWT |
| `nexum-frontend/` | Frontend | Angular 21 |

Las tareas del backend están en `docs/tareas_codigo_nexum_v2.md`.

## Variables de entorno del backend

| Variable | Obligatoria | Descripción |
|---|---|---|
| `DB_URL` | No (local) | URL **JDBC**, p. ej. `jdbc:postgresql://localhost:5432/nexum`. En la nube: `jdbc:postgresql://HOST/BASE?sslmode=require` |
| `DB_USER` | No (local) | Usuario de PostgreSQL (por defecto `postgres`) |
| `DB_PASSWORD` | Sí | Clave de PostgreSQL |
| `PORT` | No | Puerto (por defecto `8081`) |
| `JWT_SECRET` | Sí | Base64 de al menos 32 bytes: `openssl rand -base64 32` |
| `JWT_EXPIRATION_MINUTES` | No | Vigencia del token (por defecto `30`) |
| `CORS_ORIGINS` | No | Orígenes permitidos separados por coma, sin `/` final (por defecto `http://localhost:4200,https://nexum-web-tau.vercel.app`) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | No | Crean el administrador inicial si no existe |
| `SHOW_SQL` | No | `true` para ver el SQL de Hibernate (solo local) |
| `DB_POOL_SIZE` | No | Conexiones máximas a la BD (por defecto `5`) |

> Las plataformas de BD suelen entregar `postgresql://usuario:clave@host/base`.
> Spring necesita el formato JDBC: `jdbc:postgresql://host/base?sslmode=require`, con el usuario y la clave en `DB_USER` y `DB_PASSWORD`.

## Ejecutar en local

```bash
# 1. Base de datos: crear una base vacía llamada nexum (las tablas las genera JPA)
# 2. Backend
export DB_PASSWORD=...  JWT_SECRET=$(openssl rand -base64 32)
mvn spring-boot:run            # http://localhost:8081  ·  comprobación: GET /api/salud

# 3. Frontend
cd nexum-frontend
npm install
npm start                       # http://localhost:4200
```

## Pruebas

```bash
mvn test                        # contextLoads() necesita PostgreSQL activo y JWT_SECRET definido
cd nexum-frontend && npm test -- --watch=false
```

## Despliegue (3 servicios)

1. **Base de datos PostgreSQL** (Neon, Supabase, etc.): crear la base `nexum` y copiar host, usuario y clave.
2. **Backend**: servicio web con el `Dockerfile` de la raíz. Cargar las variables de entorno de la tabla anterior. Comprobación de salud: `/api/salud`. En el primer arranque Hibernate crea las tablas.
3. **Frontend**: directorio base `nexum-frontend`, comando `npm ci && npm run build`, carpeta de salida `dist/nexum-frontend/browser`. Antes, poner la URL pública del backend en `nexum-frontend/src/environments/environment.production.ts`.
4. Poner la URL del frontend en `CORS_ORIGINS` del backend y reiniciarlo.

## Equivalencias de nombres (D4)

`Categoria` = área de aprendizaje · `Examen` = evaluación.

## Consultas JPQL

| Repositorio | Consulta | Parámetros | Endpoint |
|---|---|---|---|
| `CursoRepository.buscarPorCategoria` | `SELECT c FROM Curso c JOIN c.categorias cat WHERE cat.nombre = :nombre AND c.estado = :estado` | nombrados | `GET /api/cursos?categoria=…&estado=…` |
| `InscripcionRepository.buscarPorEmail` | `SELECT i FROM Inscripcion i JOIN i.usuario u WHERE u.email = ?1` | posicional | `GET /api/inscripciones?email=…` |
| `LeccionRepository.buscarPorCurso` | `SELECT l FROM Leccion l JOIN l.modulo m WHERE m.curso.id = :cursoId ORDER BY m.orden, l.orden` | nombrado | `GET /api/lecciones?cursoId=…` |
