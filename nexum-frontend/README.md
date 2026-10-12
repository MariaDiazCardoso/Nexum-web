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
- `/admin`: gestión de cursos, personas y categorías; requiere el rol `ADMINISTRADOR`.

El backend actual requiere autenticación para listar cursos y categorías. Por eso, el catálogo muestra un acceso a inicio de sesión si la API responde `401`.

El panel administrador consulta los endpoints existentes de cursos, usuarios y categorías. Permite crear, actualizar y eliminar cursos y categorías; también cambiar roles y verificar correos. El backend es quien aplica los permisos; el rol guardado en la sesión del navegador solo controla la navegación y la visibilidad del enlace. Las cuentas administradoras están protegidas contra eliminación desde el panel.

## Navegación y cuenta administradora

Después de iniciar sesión, una cuenta `ADMINISTRADOR` abre `/admin`; una cuenta `USUARIO` abre `/catalog`. El menú muestra el enlace al panel solo a administradores. Si se escribe `/admin` manualmente como usuario, se redirige al catálogo y el backend también rechaza las llamadas administrativas.

El registro público siempre crea cuentas `USUARIO`; no existe una contraseña ni una cuenta administradora fija en el frontend. Para crear el primer administrador, define `ADMIN_EMAIL` y `ADMIN_PASSWORD` en las variables del backend antes de iniciar/desplegarlo. El backend crea esa cuenta si el email todavía no existe. Si ya hay una cuenta con ese email, no la convierte automáticamente en administradora; una cuenta administradora existente puede cambiar el rol desde el panel.

## Compilar y probar

```powershell
npm run build
npm test -- --watch=false
```

## Despliegue en Vercel

Al importar el repositorio en Vercel, configura `nexum-frontend` como **Root Directory**. Usa `npm run build` como comando de compilación y `dist/nexum-frontend/browser` como directorio de salida.

Antes de desplegar, configura la URL pública del backend en `src/environments/environment.production.ts` como `apiUrl` (incluye `/api`) y permite el dominio del frontend en la variable `CORS_ORIGINS` del backend. No pongas credenciales de PostgreSQL/Neon en Angular: el frontend solo debe consumir la API del backend.
