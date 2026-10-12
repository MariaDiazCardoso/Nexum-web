# Nexum Frontend

Frontend Angular para Nexum. El proyecto se mantiene separado del backend Spring Boot en la carpeta `nexum-frontend/`.

## Requisitos

- Node.js compatible con Angular CLI 21
- npm
- Backend Nexum iniciado para enviar solicitudes a la API

## Desarrollo local

Desde esta carpeta:

```powershell
npm ci
npm start
```

Abre `http://localhost:4200`. La API local se configura en `src/app/core/api.config.ts` y por defecto apunta a `http://localhost:8081/api`.

## Interfaces incluidas

- `/login`: inicio de sesión con JWT.
- `/register`: creación de cuenta.
- `/catalog`: búsqueda y filtros del catálogo de cursos publicados.

El backend actual requiere autenticación para listar cursos y categorías. Por eso, el catálogo muestra un acceso a inicio de sesión si la API responde `401`.

## Compilar y probar

```powershell
npm run build
npm test -- --watch=false
```

## Despliegue en Vercel

Al importar el repositorio en Vercel, configura `nexum-frontend` como **Root Directory**. Usa `npm run build` como comando de compilación y `dist/nexum-frontend/browser` como directorio de salida.

Antes de desplegar, configura la URL pública del backend en `src/environments/environment.production.ts` como `apiUrl` (incluye `/api`) y permite el dominio del frontend en la variable `CORS_ORIGINS` del backend. No pongas credenciales de PostgreSQL/Neon en Angular: el frontend solo debe consumir la API del backend.
