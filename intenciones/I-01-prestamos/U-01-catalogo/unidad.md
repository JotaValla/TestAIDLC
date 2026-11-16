# U-01 · Catálogo de libros y ejemplares

**Intención:** I-01 · **Dominio:** Catálogo · **Estado:** en curso
**Spec:** ninguna (unidad sustancial, ver `../intent.md`)

## Objetivo
Que la biblioteca pueda registrar, consultar, editar y dar de baja sus libros y los ejemplares
físicos de cada libro, y que otros dominios puedan consultar un ejemplar sin tocar Catálogo.

## Alcance
- Incluye: alta, consulta, listado/búsqueda, edición y baja de libros; alta, listado y baja de
  ejemplares (perdido o dañado); servicio público de consulta de ejemplares para otros dominios.
- Fuera: disponibilidad para préstamo (la deriva Préstamos en U-03), socios (U-02).

Respuestas del equipo (Mob Construction, 2026-11-16), citadas tal cual:
1. ISBN: "13 digitos"
2. Código de ejemplar: "lo genera el sistema"
3. Baja de ejemplar perdido o dañado en esta unidad: "si"
4. Edición y baja de libros: "si entran en u01"
5. Servicio público (B-04): "ahora"
6. Rama base: "primero mergeamos a main"

## Enfoque acordado (Mob Construction, 2026-11-16)
- Paquete `com.jotavalla.testworkflow.catalogo` con `api/` (controladores + DTOs `record`),
  `dominio/` (`Libro`, `Ejemplar`, `CatalogoService`) e `infraestructura/` (repositorios JPA).
- Esquema solo por Flyway (migraciones `V1__…`, `V2__…`); Hibernate en `validate`.
- Errores de negocio con excepciones propias de Catálogo → `@RestControllerAdvice` → `ProblemDetail`
  (400 validación, 404 no existe, 409 conflicto).
- Catálogo guarda si un ejemplar está dado de baja, pero **no** si está prestado: eso lo deriva Préstamos.
- Pruebas: reglas de dominio con prueba unitaria; endpoints con MockMvc sobre Postgres de
  Testcontainers; nombres `debe<Resultado>Cuando<Condicion>()`.

### Supuestos a confirmar al empezar cada bolt
- S-1 (B-01): "13 digitos" se valida como exactamente 13 dígitos numéricos, sin guiones.
  Resuelto 2026-11-16: "no no valides solo que tenga 13 numeros" → sin dígito de control.
- S-2 (B-03): el código de ejemplar lo genera una secuencia de la base de datos.
- S-3 (B-05): la baja de un libro es lógica (no se borra la fila). ¿Qué pasa si tiene ejemplares activos?

## Bolts

### B-01 · Alta y consulta de libro — estado: en revisión
Criterios:
- [x] Registrar un libro con ISBN, título y autor → 201 con su ubicación.
- [x] ISBN duplicado → 409 `ProblemDetail`.
- [x] ISBN que no tiene 13 dígitos, o título/autor vacío → 400 `ProblemDetail`.
- [x] Consultar un libro por id → 200; si no existe → 404 `ProblemDetail`.
Tareas:
- [x] T-01 Migración de la tabla de libros (`V1__catalogo_libros.sql`, ISBN `VARCHAR(13)` + `uk_libro_isbn`).
- [x] T-02 Entidad `Libro`, repositorio y `CatalogoService.registrarLibro` / `obtenerLibro`.
- [x] T-03 Endpoints `POST /libros` y `GET /libros/{id}` + manejo de errores.
Evidencia:
- RED: `./mvnw test` → 15 pruebas nuevas fallan por la razón correcta (404 sin endpoint,
  `UnsupportedOperation` en el servicio). Antes, `ddl-auto=validate` detectó que `CHAR(13)` no
  coincidía con el mapeo `String`; se cambió a `VARCHAR(13)`.
- Comando: `./mvnw verify` → resultado: BUILD SUCCESS, 20 pruebas, 0 fallos, 0 errores (2026-11-16,
  tras las correcciones de la revisión).
- Revisión: `gentle-ai review` (lentes risk, resilience, reliability) → resultado: **approved**, sin
  bloqueos; 1 WARNING y 4 SUGGESTION, todas corregidas:
  - R1-001, R3-broad-integrity-catch, R4-dive-cause-dropped: el `catch` convertía cualquier
    `DataIntegrityViolationException` en 409 y perdía la causa → ahora solo traduce la violación de
    `uk_libro_isbn`, conserva la causa y propaga las demás.
  - R3-size-boundary-untested: faltaba probar el límite de 255 → prueba nueva.
  - R3-fixed-isbn-shared-db: ISBN fijos en base compartida → ISBN único por prueba.
  - La revisión del candidato ya corregido fue **omitida por decisión del equipo** (consentimiento
    declinado); el "approved" corresponde al candidato previo a las correcciones.
- Pruebas nuevas:
  - `CatalogoServiceTest`: `debeRegistrarLibroCuandoElIsbnNoExiste`,
    `debeLanzarIsbnDuplicadoCuandoElIsbnYaExiste`,
    `debeLanzarIsbnDuplicadoConservandoLaCausaCuandoLaBaseRechazaElIsbnRepetido`,
    `debePropagarLaViolacionCuandoLaBaseRechazaOtraRestriccion`, `debeDevolverLibroCuandoElIdExiste`,
    `debeLanzarLibroNoEncontradoCuandoElIdNoExiste`.
  - `LibroControllerTest` (MockMvc + Postgres Testcontainers): `debeResponder201ConUbicacionCuandoLosDatosSonValidos`,
    `debeResponder409CuandoElIsbnYaExiste`, `debeResponder409CuandoLaBaseRechazaUnIsbnRepetidoEnUnaCarrera`,
    `debeResponder400CuandoElIsbnNoTiene13Digitos` (5 casos), `debeResponder400CuandoElTituloEstaVacio`,
    `debeResponder400CuandoElAutorEstaVacio`, `debeResponder400CuandoElTituloSuperaLos255Caracteres`,
    `debeDevolverLibroCuandoExiste`, `debeResponder404CuandoElLibroNoExiste`.
- PR: pendiente (rama `U-01/B-01-alta-libro`, sin commit todavía).

### B-02 · Listado y búsqueda de libros — estado: pendiente
Criterios:
- [ ] Listar libros con paginación.
- [ ] Filtrar por título y/o autor (coincidencia parcial, sin distinguir mayúsculas).
Tareas:
- [ ] T-01 Consulta paginada con filtros en el repositorio.
- [ ] T-02 Endpoint `GET /libros?titulo=&autor=&page=&size=`.
Evidencia:
- Comando: `./mvnw verify` → resultado: …
- Revisión: `gentle-ai review` → resultado: …
- Pruebas nuevas: …
- PR: …

### B-03 · Ejemplares de un libro — estado: pendiente
Criterios:
- [ ] Agregar un ejemplar a un libro existente → 201, con código generado por el sistema; libro
      inexistente → 404.
- [ ] El código de ejemplar es único.
- [ ] Listar los ejemplares de un libro.
- [ ] Dar de baja un ejemplar indicando el motivo (perdido o dañado); ejemplar inexistente → 404;
      ejemplar ya dado de baja → 409.
Tareas:
- [ ] T-01 Migración de la tabla de ejemplares (código único, estado, motivo de baja).
- [ ] T-02 Entidad `Ejemplar` y reglas de alta y baja.
- [ ] T-03 Endpoints `POST /libros/{id}/ejemplares`, `GET /libros/{id}/ejemplares`,
      `POST /ejemplares/{codigo}/baja`.
Evidencia:
- Comando: `./mvnw verify` → resultado: …
- Revisión: `gentle-ai review` → resultado: …
- Pruebas nuevas: …
- PR: …

### B-04 · Servicio público para otros dominios — estado: pendiente
Criterios:
- [ ] `CatalogoService` permite consultar un ejemplar por código (si existe, a qué libro pertenece y
      si está dado de baja) y devuelve un `record`, nunca la entidad.
- [ ] Una prueba falla si una clase fuera de `catalogo` importa `catalogo.dominio`.
Tareas:
- [ ] T-01 `record` público de consulta y método en `CatalogoService`.
- [ ] T-02 Prueba de frontera de dominio.
Evidencia:
- Comando: `./mvnw verify` → resultado: …
- Revisión: `gentle-ai review` → resultado: …
- Pruebas nuevas: …
- PR: …

### B-05 · Edición y baja de libro — estado: pendiente
Criterios:
- [ ] Editar título y autor de un libro → 200; libro inexistente → 404; datos inválidos → 400.
- [ ] Dar de baja un libro → 204; libro inexistente → 404 (regla con ejemplares activos: ver S-3).
Tareas:
- [ ] T-01 Reglas de edición y baja en `Libro`.
- [ ] T-02 Endpoints `PUT /libros/{id}` y `DELETE /libros/{id}`.
Evidencia:
- Comando: `./mvnw verify` → resultado: …
- Revisión: `gentle-ai review` → resultado: …
- Pruebas nuevas: …
- PR: …

## Avance y siguiente paso
- 2026-11-16: unidad aprobada; creado este documento.
- 2026-11-16: `chore/configuracion-base` mergeada a `main`; S-1 resuelto; B-01 construido en
  `U-01/B-01-alta-libro` y en revisión.
- Para el revisor: `spring.mvc.problemdetails.enabled=true` es global, así que cambia a `ProblemDetail` el
  formato de todos los errores de MVC (los 400 de validación salen de ahí). `CatalogoService` importa
  `org.hibernate.exception.ConstraintViolationException` para reconocer `uk_libro_isbn`.
- Siguiente: ✔ del revisor humano de B-01; después, `/bolt` para B-02.

## Despliegue
- Cómo se despliega: por definir.
- Pruebas de humo: por definir.
- Cómo revertir: por definir.
