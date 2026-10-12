# NEXUM — Tareas de implementación del backend (para IA ejecutora) — v2

Complementa `contexto_faltante_v2.md` (requisitos y modelo de datos). Este archivo solo contiene tareas ejecutables. Fuentes: cuaderno de API REST (S01–S04) y cuaderno de Spring Security (S06 JPA/Hibernate, S07 JPQL, S08 Spring Security y roles, S09 JWT). El informe v2 aún no recoge S06–S09; las reglas nuevas están aquí.

## 0. Contexto y reglas de ejecución

**Stack:** Java 21, Spring Boot 4.1.1, Maven, PostgreSQL, Lombok, Spring Security y jjwt.

**Estado actual:** solo existe el módulo Usuario (`Usuario`, `RolUsuario`, `UsuarioRepository`, `UsuarioService` como clase, `UsuarioController` en `/api/usuarios`) y la prueba vacía `contextLoads()`. Todo lo demás está pendiente.

**Fuente de verdad del modelo:** diagrama `Nexum_bd.pdf` (25 tablas).

**Restricciones del docente que condicionan el código:**
* Capas modelo → repositorio → servicio → controlador. El servicio se declara como **interfaz más clase de implementación** (S06).
* La persistencia es con JPA/Hibernate: el modelo genera las tablas (`ddl-auto=update`) y el repositorio extiende `JpaRepository` (S06).
* El proyecto debe evidenciar **al menos 2 consultas JPQL** expuestas por controlador (S07).
* Seguridad con Spring Security: `SecurityFilterChain`, BCrypt, roles y **JWT** con filtro propio (S08, S09).
* URLs con sustantivos, métodos HTTP correctos incluido `PATCH`, y TDD con casos con error y sin error (S01–S04).
* La semana 10 es la presentación del Avance 2: debe mostrar persistencia con BD, JPQL y la seguridad con JWT. La ruta mínima está en la sección 4.

**Reglas de ejecución:**
1. Seguir el orden de los IDs, respetando el campo *Depende de*.
2. En cada tarea: escribir las pruebas primero, ejecutar `mvn test` y confirmar que fallan (rojo); implementar hasta que pasen (verde); refactorizar.
3. Una tarea está terminada cuando cumple su *Aceptación*, `mvn test` está en verde y no queda código muerto.
4. Lo marcado con ⚠ es un supuesto no definido por el diagrama ni por el docente: implementarlo como se indica y listarlo en el reporte final (T17).
5. Al cerrar cada tarea, reportar archivos creados o modificados, pruebas y supuestos aplicados.

---

## 1. Convenciones globales (aplican a todas las tareas)

**Notación de campos:** `campo Tipo(longitud)`; `*` = obligatorio (⚠ contrastar con los `NOT NULL` reales de la BD, T01); `→X` = relación `@ManyToOne(fetch = LAZY)` hacia X con `@JoinColumn(name = "<columna>_id")` según el diagrama; `(RO)` = solo lectura o derivado, no se acepta en las solicitudes.

**Entidades**
* Anotaciones como en `Usuario`: `@Entity @Table(name = …) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`. No usar `@Data`.
* ID `UUID` con `@GeneratedValue(strategy = GenerationType.UUID)`, salvo donde se indique.
* Columnas con `@Column(name = "snake_case", length = …, nullable = …, unique = …)` según el diagrama y la marca `*`.
* Fechas `LocalDateTime` asignadas en `@PrePersist`.
* Enums con `@Enumerated(EnumType.STRING)`.
* Relaciones unidireccionales `@ManyToOne`; consultar hijos con métodos del repositorio. Solo se usa `@OneToMany` donde la tarea lo pide.
* Con `@Builder`, los valores por defecto llevan `@Builder.Default`.

**Capas de cada módulo**
* `XRepository extends JpaRepository<X, UUID>`. No lleva `@Repository`.
* `XService` es una **interfaz** con los métodos de negocio y `XServiceImpl` es la clase con `@Service`. Constructor generado con Lombok (`@RequiredArgsConstructor`, campos `final`). Los controladores y las pruebas dependen de la interfaz.
* `XController` con `@RestController` y `@RequestMapping("/api/<plural-minúscula-con-guiones>")`, con el servicio inyectado por constructor.
* `@Transactional` ⚠ en los métodos de `XServiceImpl` que modifican más de una entidad (T08, T10, T14, T15). El título de S07 anuncia transacciones, pero el material no desarrolla el tema.
* DTOs como `record` en `com.nexum.dto`: `XRequest` (crear y PUT, con validación Jakarta), `XPatchRequest` (todos los campos opcionales, solo donde se indique) y `XResponse` (con ids de las relaciones, nunca entidades anidadas).
* Mapeo manual con un método estático `desde(entidad)` en el `XResponse`. No añadir MapStruct.
* Las entidades nunca salen en las respuestas ni entran como `@RequestBody`.
* Las listas devuelven `List`, sin paginación.
* Las consultas propias se escriben en JPQL con `@Query` en el repositorio, sobre nombres de entidad y atributo (no de tabla y columna). Se usan parámetros posicionales (`?1`) o nombrados (`:x` con `@Param`).

**CRUD estándar** (cada tarea indica filtros, `PATCH` y extras)

| Operación | Ruta | Códigos |
|---|---|---|
| Crear | `POST /api/x` | 201; 400 inválido; 404 referencia inexistente; 409 duplicado |
| Listar | `GET /api/x` | 200 |
| Obtener | `GET /api/x/{id}` | 200; 404 |
| Reemplazar | `PUT /api/x/{id}` | 200; 400; 404; 409 |
| Parcial | `PATCH /api/x/{id}` (solo si la tarea lo indica) | 200; 400; 404; 409 |
| Eliminar | `DELETE /api/x/{id}` | 204; 404; 409 si tiene dependientes (no hay borrado en cascada) |

Los filtros son parámetros de consulta sobre rutas planas (`GET /api/modulos?cursoId=`). Las tablas intermedias sin id propio se exponen como subrecurso anidado.

**Errores:** siempre mediante `GlobalExceptionHandler` (T02); los servicios lanzan excepciones propias, nunca `IllegalArgumentException`.

**Pruebas** (JUnit 5 + Mockito, sin contexto Spring ni BD, salvo T11a y T11b)
* `XServiceImplTest`: repositorios simulados. Mínimo: crear (ok y dato inválido o duplicado), obtener (ok e inexistente) y la regla de negocio principal.
* `XControllerTest`: `new XController(servicioSimulado)` sobre la interfaz; verificar código de estado y cuerpo, y `verify(servicio, times(1))…`.
* Validación de DTOs: probar con `jakarta.validation.Validator`.
* `contextLoads()` sí necesita PostgreSQL activo; documentarlo.
* Si se usa `@WebMvcTest` o `@AutoConfigureMockMvc`, verificar los imports: en Boot 4 los paquetes de las pruebas se reorganizaron.

---

## 2. Decisiones por defecto (confirmar con el usuario si impactan)

| ID | Decisión por defecto |
|---|---|
| D1 | Nombres de tabla: los del diagrama en minúscula snake_case (`usuario`, `curso`, `curso_categoria`, `opcion_respuesta`, `ia_historial`…). Como JPA crea las tablas desde `@Table`, estos nombres son los que existirán en la BD. Antes de T03, ejecutar `\dt`; si ya existen con otros nombres (p. ej. `usuarios` o `"Usuario"` entre comillas), mapear exactamente esos o eliminarlas |
| D2 | Se crea la entidad y tabla `Actividad`, porque la lista de clases solicitada la incluye y el diagrama no la tiene. Actualizar el diagrama y el informe |
| D3 | Solo son `enum`: `RolUsuario` y `EstadoCurso` (`BORRADOR`, `REVISION`, `PUBLICADO`, `ARCHIVADO`; ⚠ confirmar valores). Los demás campos categóricos (`nivel`, `tipo`, `dificultad`, `plan`, `metodo`, `estado` de Inscripcion, Pago y Suscripcion, `rol` de `ia_historial`) son `String` con `@Size`, hasta conocer sus valores |
| D4 | Nombres de entidad según el diagrama: `Categoria` (= área de aprendizaje) y `Examen` (= evaluación). Documentar la equivalencia en el README |
| D5 | Rutas planas con filtros por parámetro de consulta (sección 1) |
| D6 | `ddl-auto=update`: el modelo JPA crea y actualiza las tablas, como en S06. Pasar a `validate` solo si la BD se crea con un script DDL externo |
| D7 | `DELETE` responde 204. El docente muestra 200 con un mensaje; ambos son 2xx |
| D8 | jjwt 0.12.x (API `Jwts.builder()` y `Jwts.parser().verifyWith(…)`). El docente usa 0.11.5; si se prefiere fijar esa versión, adaptar la API |

---

## 3. Tareas

### Fase A — Base

#### T01 · Configuración y verificación del proyecto
**Depende de:** —
1. Ejecutar `mvn clean test`; confirmar que compila y que JUnit 5 y Mockito están disponibles. Si faltan, añadir `spring-boot-starter-test` (scope `test`).
2. Crear solo la base `nexum` vacía en PostgreSQL (como en S06, las tablas las genera JPA). Si ya existen tablas, comparar `\dt` y `\d <tabla>` con el diagrama y aplicar D1; anotar `NOT NULL`, `UNIQUE` y FK reales para usarlos en las tareas siguientes.
3. Ajustar `application.properties`:
   * Variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD` (sin valor por defecto para la clave) y `PORT` (por defecto 8081).
   * `spring.jpa.hibernate.ddl-auto=update` (D6). Al arrancar, revisar en consola que las tablas generadas respetan nombres, longitudes y tipos del diagrama.
   * Mantener el dialecto de PostgreSQL (el docente lo configura; si Hibernate avisa que es innecesario, es solo una advertencia). Si el equipo cambia de motor, se cambian la dependencia, la URL y el dialecto.
   * `show-sql` solo para uso local.

**Aceptación:** `mvn test` en verde con PostgreSQL activo; la aplicación arranca y crea las tablas en la BD vacía; no quedan credenciales en el repositorio.

#### T02 · Errores globales y utilidades
**Depende de:** T01
**Crear en `com.nexum.exception`:** `RecursoNoEncontradoException` (404), `ConflictoException` (409), `SolicitudInvalidaException` (400), `ErrorResponse` (record) y `GlobalExceptionHandler` con `@RestControllerAdvice`.
**Crear en `com.nexum.util`:** `SlugUtil.generar(String)` (minúsculas, sin tildes, guiones, sin caracteres especiales).

`ErrorResponse`: `timestamp`, `status`, `error`, `message`, `path`, `errores[{campo, mensaje}]` (solo en validación).

| Origen | Estado |
|---|---|
| Las tres excepciones propias | 404, 409, 400 |
| `MethodArgumentNotValidException` | 400 con `errores` por campo |
| `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException` (p. ej. UUID inválido) | 400 |
| `DataIntegrityViolationException` | 409 |
| `Exception` | 500 con mensaje genérico; registrar el detalle en el log, sin exponer la traza |

**Pruebas:** un caso por fila de la tabla, y `SlugUtil` (tildes, espacios, símbolos).
**Aceptación:** ninguna ruta devuelve un error con formato distinto de `ErrorResponse`.

#### T03 · Refactor del módulo Usuario
**Depende de:** T02. Añadir `spring-security-crypto` (solo `BCryptPasswordEncoder`; T11a lo retira porque el starter de seguridad ya lo incluye) y declarar el bean `PasswordEncoder` en `config/PasswordConfig`, aparte de la futura `SecurityConfig` para evitar dependencias circulares.

**Estructura de servicio:** extraer la interfaz `UsuarioService` y renombrar la clase actual a `UsuarioServiceImpl` (`@Service`).

**DTOs**
* `UsuarioRegistroRequest`: `nombre*(≤100)`, `apellido*(≤100)`, `email*(@Email, ≤255)`, `password*(8–72)`.
* `UsuarioRequest` (PUT): `nombre*`, `apellido*`, `email*`, `avatarUrl(≤255)`.
* `UsuarioPatchRequest`: los mismos campos, todos opcionales.
* `UsuarioResponse`: `id, nombre, apellido, email, rol, puntosXp, avatarUrl, fechaRegistro, ultimaConexion, emailVerificado`. No incluye `passwordHash`.

**Servicio**
* Normalizar el email (`trim` y minúsculas ⚠).
* Email duplicado → `ConflictoException`. En actualización excluir al propio usuario (`existsByEmailAndIdNot`).
* Guardar `passwordHash` con BCrypt.
* El registro fuerza `rol = USUARIO`.
* `rol` y `emailVerificado` ya no se modifican por PUT ni PATCH (un endpoint de administrador los gestionará en T11a).
* Reemplazar todos los `IllegalArgumentException` por `RecursoNoEncontradoException` o `ConflictoException`.

**Entidad:** aplicar D1 al nombre de tabla; `email` con `unique = true`; corregir los inicializadores `puntosXp = 0` y `emailVerificado = false` con `@Builder.Default` (el `@PrePersist` los mantiene).
**Controlador:** `@Valid` + DTOs; añadir `PATCH`; mantener 201 y 204.
**Pruebas:**
* Servicio: registro ok, email duplicado, obtener inexistente, PUT con cambio de email a uno existente, hash distinto de la contraseña, `rol` forzado a `USUARIO`.
* Controlador: códigos de estado.
* DTOs: validaciones.

**Aceptación:** ninguna respuesta contiene `passwordHash`; el cliente no puede fijar `rol`; `UsuarioService` es interfaz; pruebas en verde.

### Fase B — Núcleo educativo (P1)

#### T04 · Categoria
**Depende de:** T02
**Campos:** `nombre String(100)*`, `iconoUrl String(255)`.
**Endpoints:** CRUD estándar. `DELETE` → 409 si tiene cursos asociados.
**Pruebas:** crear ok y sin nombre, eliminar con cursos asociados (409).

#### T05 · Curso (con Curso_Categoria)
**Depende de:** T03, T04
**Campos**
* `titulo String(255)*`
* `slug String(255)` único (se genera con `SlugUtil` si no llega; colisión → 409)
* `descripcion text`
* `responsable→Usuario*` (columna `responsable_id`; debe existir y tener rol `ADMINISTRADOR` ⚠)
* `precio BigDecimal(10,2)` ≥ 0
* `nivel String(20)`
* `estado EstadoCurso` (por defecto `BORRADOR`)
* `promedioValoracion BigDecimal(3,2)` (RO)
* `fechaCreacion` (RO)
* `recomendadoPara text`
* `duracionEstimada String(50)`
* `ruta String(50)`
* `categoriaAcademica String(80)`

**Relación con Categoria:** `@ManyToMany` con `@JoinTable(name = "curso_categoria", joinColumns = curso_id, inverseJoinColumns = categoria_id)`. La solicitud recibe `categoriaIds` (cada id debe existir → 404); la respuesta devuelve `categoriaIds` y `responsableId`.
**Endpoints:** CRUD estándar + `PATCH` (incluye cambio de `estado`). Filtros: `categoriaId`, `estado`. `DELETE` → 409 si tiene módulos (T09 añadirá la verificación de inscripciones).
**Pruebas:** crear ok, responsable inexistente o no administrador, categoría inexistente, slug duplicado, `PATCH` de estado.

#### T06 · Modulo
**Depende de:** T05
**Campos:** `curso→Curso*`, `titulo String(255)*`, `orden Integer*` (≥ 1), `descripcion text`, `slug String(180)`.
**Reglas:** `orden` único dentro del curso → 409 ⚠.
**Endpoints:** CRUD estándar. Filtro: `cursoId` (ordenado por `orden`). `DELETE` → 409 si tiene lecciones.

#### T07 · Leccion y Actividad
**Depende de:** T06
**Leccion:** `modulo→Modulo*`, `titulo String(255)*`, `tipo String(20)`, `orden Integer*`, `contenidoTexto text`, `puntosOtorgados Integer`, `videoUrl text`, `xpOtorgada Integer`, `slug String(180)`, `retoPractico text`, `dificultad String(20)`, `duracionEstimada String(30)`, `duracionMinima Integer`, `imagenUrl text`. No tiene `estado`.
**Actividad** (D2): `leccion→Leccion*`, `titulo String(255)*`, `instrucciones text`, `tipo String(30)`.
**Endpoints:** CRUD estándar para ambas. Filtros: `moduloId`, `leccionId`. `orden` único por módulo → 409 ⚠. `DELETE` de una lección → 409 si tiene dependientes (se amplía en T08, T10, T12 y T13).

#### T08 · Examen, Pregunta y OpcionRespuesta
**Depende de:** T07
**Examen:** `leccion→Leccion*`, `titulo String(255)*`, `intentosMaximos Integer` (≥ 1), `puntajeMinimoAprobacion BigDecimal(5,2)` (≥ 0). El diagrama sugiere relación 1:1 con lección: verificar `UNIQUE` en la BD y, si existe, responder 409 al duplicar.
**Pregunta:** `examen→Examen*`, `textoPregunta text*`, `explicacionRespuesta text`. Tiene `@OneToMany(mappedBy = "pregunta", cascade = ALL, orphanRemoval = true) List<OpcionRespuesta>` (único caso bidireccional).
**OpcionRespuesta:** `pregunta→Pregunta*`, `textoOpcion text*`, `esCorrecta Boolean*`.
**Endpoints:** `/api/examenes` (filtro `leccionId`) y `/api/preguntas` (filtro `examenId`), CRUD estándar. Las opciones viajan dentro de la solicitud y la respuesta de la pregunta (`opciones[{textoOpcion, esCorrecta}]`); `PUT` reemplaza todo el conjunto, en un método `@Transactional`. No tienen endpoint propio.
**Reglas:** una pregunta debe tener al menos 2 opciones y al menos 1 correcta → 400 ⚠.
**Nota:** la respuesta incluye `esCorrecta`; T11a la oculta a rol `USUARIO`.

### Fase C — Seguimiento (P1)

#### T09 · Inscripcion
**Depende de:** T03, T05
**Campos:** `usuario→Usuario*`, `curso→Curso*`, `estado String(20)*` (por defecto `"ACTIVA"` ⚠), `progresoPorcentaje BigDecimal(5,2)` (RO, 0 al crear), `fechaInscripcion` (RO).
**Reglas:** único por (usuario, curso) → 409; el curso debe estar `PUBLICADO` → 400 ⚠.
**Endpoints:** `POST`, `GET` (filtros `usuarioId`, `cursoId`), `GET /{id}`, `PATCH /{id}` (solo `estado`), `DELETE`.
**Además:** actualizar el `DELETE` de `CursoServiceImpl` para devolver 409 si hay inscripciones.

#### T10 · ProgresoLeccion
**Depende de:** T07, T09
**Campos:** `usuario→Usuario*`, `leccion→Leccion*`, `completado Boolean` (por defecto false), `ultimoSegundoVisto Integer`, `fechaCompletado` (RO), `tiempoActivo Integer`.
**Reglas**
* Único por (usuario, leccion) → 409.
* El usuario debe tener inscripción activa en el curso de la lección → 400.
* Al marcar `completado` se asigna `fechaCompletado`; al desmarcar se anula.
* Cada cambio de `completado` recalcula, en un método `@Transactional`, `Inscripcion.progresoPorcentaje = 100 × lecciones completadas del usuario en el curso / total de lecciones del curso`, con 2 decimales. Los conteos son consultas JPQL con `@Query` (se suman a las de T10b).
* Primera vez que se completa una lección: sumar `leccion.xpOtorgada` a `usuario.puntosXp` (idempotente) ⚠.

**Endpoints:** `POST`, `GET` (filtros `usuarioId`, `cursoId`, `leccionId`), `GET /{id}`, `PATCH /{id}` (`completado`, `ultimoSegundoVisto`, `tiempoActivo`), `DELETE`.
**Pruebas:** cálculo de porcentaje (0, parcial y 100), sin inscripción (400), duplicado (409), XP no repetido.

#### T10b · Consultas JPQL obligatorias
**Depende de:** T05, T07, T09
**Requisito del docente (S07):** el proyecto debe evidenciar al menos 2 consultas JPQL, con parámetros posicionales y nombrados, y al menos una con `JOIN`. Cada una se define con `@Query` en el repositorio, pasa por el servicio y se expone en el controlador.

| Repositorio y método | Consulta | Parámetros | Endpoint |
|---|---|---|---|
| `CursoRepository.buscarPorCategoria` | `SELECT c FROM Curso c JOIN c.categorias cat WHERE cat.nombre = :nombre AND c.estado = :estado` | nombrados | `GET /api/cursos?categoria=…&estado=…` (`estado` por defecto `PUBLICADO` ⚠) |
| `InscripcionRepository.buscarPorEmail` | `SELECT i FROM Inscripcion i JOIN i.usuario u WHERE u.email = ?1` | posicional | `GET /api/inscripciones?email=…` |
| `LeccionRepository.buscarPorCurso` | `SELECT l FROM Leccion l JOIN l.modulo m WHERE m.curso.id = :cursoId ORDER BY m.orden, l.orden` | nombrado | `GET /api/lecciones?cursoId=…` |

**Pruebas:** servicio (con resultados y lista vacía) y controlador (200 y parámetro faltante → 400).
**Aceptación:** los tres endpoints devuelven datos reales de la BD; hay un ejemplo posicional, uno nombrado y dos con `JOIN`; las consultas quedan documentadas en el README (T17) para mostrarlas en el avance.

### Fase D — Seguridad (S08 y S09)

#### T11a · Spring Security base: usuarios desde la BD, roles y administrador inicial
**Depende de:** T10b. Se puede hacer seguida de T11b; HTTP Basic es solo el paso intermedio de S08 y T11b lo elimina. No implementar usuarios en memoria (paso didáctico de S08): usar directamente la BD.
**Dependencias:** `spring-boot-starter-security` (retirar `spring-security-crypto` de T03) y el starter de pruebas de seguridad que corresponda a Boot 4 (verificar el nombre en Initializr).

**Archivos**
* `config/SecurityConfig` con `@Configuration @EnableWebSecurity @EnableMethodSecurity`: bean `SecurityFilterChain` con `authorizeHttpRequests` y `requestMatchers` según la matriz; `/api/auth/**` con `permitAll()`; el resto autenticado; `csrf(csrf -> csrf.disable())`; sesión sin estado (`STATELESS`); HTTP Basic provisional.
* `security/UsuarioDetails implements UserDetails`: `id`, `email`, `passwordHash` y la autoridad `ROLE_<rol>`.
* `service/UsuarioDetailsService implements UserDetailsService`: carga por email con `UsuarioRepository.findByEmail`; si no existe, `UsernameNotFoundException`.
* Proveedor de autenticación `DaoAuthenticationProvider` con ese servicio y el `PasswordEncoder` (verificar la firma en la versión de Spring Security de Boot 4) y bean `AuthenticationManager` obtenido de `AuthenticationConfiguration`.
* `config/DataInitializer` (`CommandLineRunner`, carga inicial como en S08): crea un usuario `ADMINISTRADOR` con `ADMIN_EMAIL` y `ADMIN_PASSWORD` (variables de entorno, sin valores por defecto) si no existe; si faltan las variables, omitirlo y registrar un aviso. Sin él no habría forma de crear el primer administrador, porque el registro fuerza `USUARIO`.
* `AuthenticationEntryPoint` (401) y `AccessDeniedHandler` (403) que respondan con `ErrorResponse`. Añadir `AccessDeniedException` a `GlobalExceptionHandler` (403).
* `AccesoService.verificar(UUID usuarioId)`: lanza `AccessDeniedException` si el usuario autenticado no es `ADMINISTRADOR` ni el dueño del recurso. Los servicios de datos del usuario (T09, T10, T12–T16) lo invocan.
* `PATCH /api/usuarios/{id}/rol` y `PATCH /api/usuarios/{id}/email-verificado`, solo `ADMINISTRADOR`.
* Ocultar `esCorrecta` en la respuesta de preguntas cuando el rol es `USUARIO`.

**Matriz de autorización** (los roles se verifican en `SecurityConfig` con `hasRole`; la pertenencia, con `AccesoService`)

| Recurso | Lectura | Escritura |
|---|---|---|
| `/api/auth/**` | Público | Público |
| `usuarios` | `ADMINISTRADOR` todos; `USUARIO` solo el propio | `ADMINISTRADOR` todos; `USUARIO` solo el propio (`PUT`/`PATCH`); `DELETE` solo `ADMINISTRADOR` |
| Catálogo: categorías, cursos, módulos, lecciones, actividades, exámenes, preguntas, recursos, videos | Cualquier autenticado | Solo `ADMINISTRADOR` |
| Datos del usuario: inscripciones, progresos, seguimientos, comentarios, `ia_*` | Solo si `usuarioId` es el propio o `ADMINISTRADOR` | Igual |
| Emitidos por el sistema: calificaciones, certificados, medallas asignadas, suscripciones, pagos, códigos de verificación | Propios o `ADMINISTRADOR` | Solo `ADMINISTRADOR` ⚠ |

**Pruebas:** con `@WebMvcTest` (sin BD) importando `SecurityConfig` y los beans de seguridad, con `@MockitoBean` para servicios y repositorios; verificar los imports de Boot 4. Casos: HTTP Basic válido e inválido, 401 sin credenciales, 403 de `USUARIO` en ruta de administrador, acceso al recurso propio y rechazo al ajeno, `esCorrecta` oculta, creación del administrador inicial y su omisión si ya existe. Actualizar los constructores de los servicios de T09 y T10 en sus pruebas para inyectar `AccesoService` simulado.
**Aceptación:** ninguna ruta de datos queda sin proteger; las contraseñas se validan con BCrypt contra la BD; `mvn test` en verde.

#### T11b · JWT y CORS
**Depende de:** T11a
**Dependencias jjwt (D8):** propiedad `jjwt.version`; `jjwt-api` (compile), `jjwt-impl` y `jjwt-jackson` (runtime). Si `jjwt-jackson` (Jackson 2) choca con el Jackson 3 de Boot 4 al arrancar, usar `jjwt-gson`.
**Propiedades:** `jwt.secret=${JWT_SECRET}` (Base64 de al menos 32 bytes, p. ej. `openssl rand -base64 32`; sin valor por defecto), `jwt.expiration-minutes=${JWT_EXPIRATION_MINUTES:30}`, `cors.allowed-origins=${CORS_ORIGINS:http://localhost:4200}`.

**Archivos**
* `service/JwtService`:
  * `generarToken(UsuarioDetails)` con `subject = email` y claims `uid` (id del usuario) y `authorities` (lista, p. ej. `["ROLE_ADMINISTRADOR"]`).
  * Incluye `issuedAt` y `expiration`.
  * Firma HS256 con una clave HMAC derivada de `jwt.secret`.
  * `extraerEmail(token)` y `esValido(token, usuarioDetails)` (email coincide y no ha vencido).
* `filter/JwtAuthFilter` (`@Component`, extiende `OncePerRequestFilter`):
  * Lee la cabecera `Authorization: Bearer <token>`, extrae el email y carga el `UsuarioDetails` con `UsuarioDetailsService`.
  * Si el token es válido, coloca un `UsernamePasswordAuthenticationToken(userDetails, null, authorities)` en el `SecurityContextHolder`.
  * Token ausente, vencido, mal formado o con firma inválida en ruta protegida → 401 mediante el entry point.
  * Se registra con `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`; se elimina HTTP Basic de `SecurityConfig`.
* `controller/AuthController` (`/api/auth`):
  * `POST /registro` reutiliza el registro de T03 y devuelve `UsuarioResponse` (201).
  * `POST /token` recibe `{email, password}`, autentica con `AuthenticationManager` y devuelve `{token, tipo: "Bearer", expiraEn, usuario}`. Credenciales inválidas → 401 con un mensaje único para email y contraseña. Si funciona, actualiza `ultimaConexion`.
* `CorsConfigurationSource`: métodos GET, POST, PUT, PATCH, DELETE y OPTIONS; cabeceras `Authorization` y `Content-Type`.

**Pruebas:** flujo que muestra el docente (S09): ruta protegida sin token → 401; `POST /api/auth/token` devuelve token; la misma ruta con `Authorization: Bearer` → 200. Además: token vencido, token manipulado, token de `USUARIO` en ruta de administrador → 403, claims correctos en el token generado y preflight CORS.
**Aceptación:** el flujo registro → token → ruta protegida funciona con `ADMINISTRADOR` y `USUARIO`; ningún secreto está en el repositorio; las pruebas unitarias de T03–T10 siguen en verde.

### Fase E — Seguimiento ampliado y contenido de apoyo (P2)

Cada tarea de esta fase sigue la matriz de T11a y las convenciones de la sección 1.

#### T12 · SeguimientoLeccion, Calificacion y Certificado
**Depende de:** T09, T10
**SeguimientoLeccion:** `usuario→*`, `leccion→*`, `tiempoAcumulado Integer=0`, `cantidadVisitas Integer=0`, `intentosPractica Integer=0`, `primeraActividad` (RO), `ultimaActividad` (se actualiza en cada `PATCH`). Único por (usuario, leccion) → 409. Endpoints: `POST`, `GET` (`usuarioId`, `leccionId`), `GET /{id}`, `PATCH /{id}`.
**Calificacion:** `usuario→*`, `curso→*`, `leccion→Leccion` (opcional), `tipo String(30)*`, `titulo String(150)*`, `nota BigDecimal(5,2)*`, `notaMaxima BigDecimal(5,2)*`, `peso BigDecimal(5,2)`, `comentario text`, `fechaRegistro` (RO). Regla: `notaMaxima > 0` y `0 ≤ nota ≤ notaMaxima` → 400. Endpoints: CRUD estándar con filtros `usuarioId`, `cursoId`.
**Certificado:** `inscripcion→Inscripcion*` (1:1, verificar `UNIQUE`), `codigoVerificacion String(100)*` único (lo genera el servidor), `urlPdf String(255)`, `fechaEmision` (RO). Regla: solo si `progresoPorcentaje = 100` → 400. Endpoints: CRUD estándar con filtros `usuarioId` y `codigo`; la verificación es `GET /api/certificados?codigo=…` (sin verbos en la URL). No se genera el PDF; solo se guarda la URL.

#### T13 · Contenido de apoyo
**Depende de:** T07
* **RecursoAdjunto:** `leccion→*`, `nombre String(255)*`, `urlArchivo String(255)*`, `tipoArchivo String(50)`. Filtro `leccionId`.
* **VideoStreaming:** `leccion→*` (1:1, verificar `UNIQUE`), `awsS3Key String(255)*`, `awsCloudfrontUrl String(255)`, `duracionSegundos Integer`, `calidadDisponible String(50)`. Solo persistencia, sin AWS SDK.
* **ComentarioLeccion:** `usuario→*`, `leccion→*`, `comentarioPadre→ComentarioLeccion` (opcional; debe ser de la misma lección ⚠), `contenido text*`, `fecha` (RO). Filtro `leccionId` con respuesta plana que incluye `comentarioPadreId`. `DELETE` → 409 si tiene respuestas ⚠.
* **RecursoExternoCache:** `leccion→*`, `tipo String(30)`, `titulo text`, `descripcion text`, `url text*`, `thumbnail text`, `fuente String(50)`, `metadata Map<String,Object>` (columna jsonb con `@JdbcTypeCode(SqlTypes.JSON)`), `fechaCreacion` (RO). Filtro `leccionId`.

CRUD estándar para los cuatro.

#### T14 · Medalla y UsuarioMedalla
**Depende de:** T03
**Medalla:** `nombre String(100)*`, `descripcion text`, `imagenUrl String(255)`. CRUD estándar; `DELETE` → 409 si está asignada.
**UsuarioMedalla:** clave compuesta (`@EmbeddedId` con `usuarioId` y `medallaId`, relaciones con `@MapsId`), `fechaObtencion` (RO).
**Endpoints anidados** (tabla intermedia sin id propio): `POST` y `GET /api/usuarios/{usuarioId}/medallas`, `DELETE /api/usuarios/{usuarioId}/medallas/{medallaId}`. Asignar dos veces → 409.

### Fase F — Monetización, acceso e IA (P3)

#### T15 · Suscripcion, Pago y CodigoVerificacion
**Depende de:** T03, T11a
**Suscripcion:** `usuario→*`, `plan String(20)*`, `precio BigDecimal(10,2)*`, `creditosIa Integer`, `creditosRestantes Integer`, `fechaInicio*`, `fechaFin`, `renovacionAutomatica Boolean=false`, `estado String(20)*`, `createdAt` (columna `created_at`, RO). Reglas: `0 ≤ creditosRestantes ≤ creditosIa` y `fechaFin > fechaInicio` → 400. CRUD estándar con filtro `usuarioId`.
**Pago:** `usuario→*`, `suscripcion→Suscripcion` (opcional; si llega, debe pertenecer al mismo usuario → 400), `monto BigDecimal(10,2)*` (> 0), `metodo String(30)*`, `estado String(20)*`, `concepto String(80)`, `referencia String(120)`, `fechaPago` (RO). CRUD estándar con filtro `usuarioId`. Sin integración con pasarela de pago.
**CodigoVerificacion:** `usuario→*`, `tipo String(30)*`, `codigoHash text*`, `usado Boolean=false`, `expiraEn*`, `fechaCreacion` (RO). Nunca se guarda el código en claro, solo su hash con `PasswordEncoder`.
* `POST /api/codigos-verificacion` genera el código, lo devuelve una sola vez en la respuesta (el envío por correo queda fuera de alcance) y recibe `usuarioId`, `tipo` y `minutosVigencia`.
* `POST /api/codigos-verificacion/{id}/validacion` recibe el código; rechaza si está usado o vencido (400) y lo marca como `usado`, en un método `@Transactional`.
* ⚠ Si el `tipo` corresponde a verificación de correo, fijar `Usuario.emailVerificado = true`; el valor exacto del tipo se confirma con el usuario.

#### T16 · Tablas de IA
**Depende de:** T03, T07
**IaAnalisisLeccion** (tabla `ia_analisis_leccion`): `id Integer` con `GenerationType.IDENTITY`, `usuario→*`, `leccion→*`, `resumen text`, `nivel String(30)`, `duracion String(50)`, `objetivos` (jsonb, `List<String>`), `consejo text`, `siguientePaso text`, `proyecto` (jsonb, `Map<String,Object>`), `fechaCreacion` (RO).
**IaHistorial** (tabla `ia_historial`): `id Integer` con `IDENTITY`, `usuario→*`, `curso→Curso` (opcional), `leccion→Leccion` (opcional), `rol String(20)*`, `mensaje text*`, `fechaCreacion` (RO).
**Endpoints:** `/api/analisis-leccion` y `/api/historial-ia`, CRUD estándar con `@PathVariable Integer id`. Filtros `usuarioId`, `leccionId` y, en el historial, `cursoId`.
**Fuera de alcance:** llamadas a modelos de IA y descuento de `creditosRestantes`.

### Fase G — Cierre

#### T17 · Documentación y evidencia
**Depende de:** todas las anteriores que se hayan ejecutado
1. `README.md`:
   * Variables de entorno: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `PORT`, `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`, `CORS_ORIGINS`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`.
   * Cómo ejecutar y probar, y las equivalencias de nombres (D4).
   * Tabla de endpoints con métodos y códigos de estado.
   * Sección **Consultas JPQL** con las de T10b y T10 (consulta, parámetros y endpoint).
2. `docs/nexum.postman_collection.json`, importable en Thunder Client o Postman, con el flujo principal (registro → token → curso publicado → inscripción → progreso → examen), los casos con error y sin error, y los casos de seguridad: ruta protegida sin token (401), `USUARIO` en ruta de administrador (403) y token inválido.
3. `docs/evidencia-pruebas.md`: salida de `mvn test` y lista de pruebas por módulo, separando casos con error y sin error.
4. Revisión final: ninguna URL con verbos, ningún DTO expone `passwordHash`, ningún secreto en el repositorio, las tablas generadas por `ddl-auto=update` coinciden con el diagrama.
5. Reporte final con los supuestos ⚠ aplicados y las decisiones D1–D8.

**Aceptación:** `mvn test` en verde y el flujo principal ejecutable de punta a punta desde la colección.

---

## 4. Orden de ejecución

`T01 → T02 → T03 → T04 → T05 → T06 → T07 → T08 → T09 → T10 → T10b → T11a → T11b`, y después `T12`, `T13`, `T14`, `T15` y `T16` en cualquier orden (cada una depende solo de lo indicado), y por último `T17`.

**Ruta mínima para el Avance 2** (persistencia, JPQL y seguridad con JWT): `T01 → T02 → T03 → T04 → T05 → T06 → T07 → T09 → T10b → T11a → T11b → T17`. `T08` y `T10` se suman si hay tiempo, porque completan el flujo de aprendizaje.
