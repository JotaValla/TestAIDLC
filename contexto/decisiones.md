# Decisiones técnicas

Formato corto: contexto → decisión → consecuencia. Solo se agregan con aprobación de los dos devs.

## D-001 · PostgreSQL desde el inicio
- **Contexto:** ICAF usará PostgreSQL; queremos que la prueba del flujo se parezca al caso real.
- **Decisión:** PostgreSQL local para desarrollo y Testcontainers (Postgres en Docker) para las
  pruebas de integración. No usamos H2.
- **Consecuencia:** hace falta Docker para `./mvnw verify`; a cambio, las pruebas corren contra
  la misma base que producción.

## D-002 · Paquetes por dominio
- **Contexto:** queremos practicar fronteras de dominio (DDD) igual que en ICAF.
- **Decisión:** paquete raíz por dominio, capas adentro (ver `convenciones.md`).
- **Consecuencia:** algo más de estructura para un proyecto chico, a cambio de practicar.

## D-003 · Alinear el contexto con el proyecto generado
- **Contexto:** `contexto/` describía Java 21, Spring Boot 3.x, paquete `com.ejemplo.biblioteca` y
  base `biblioteca`, pero el proyecto se generó con Java 27, Spring Boot 4.1.2, paquete
  `com.jotavalla.testworkflow` y base `testworkflow`.
- **Decisión:** el código real manda. Se usan Java 27, Spring Boot 4.1.2, paquete raíz
  `com.jotavalla.testworkflow` y base `testworkflow`. Se mantienen D-001 (PostgreSQL +
  Testcontainers) y Flyway con `ddl-auto=validate`.
- **Consecuencia:** Java 27 no es LTS (la LTS vigente es 25); si ICAF exige LTS, bajar a 25 es un
  cambio de `java.version` en `pom.xml` más instalar ese JDK. Spring Boot 4 separa la
  auto-configuración por módulos: Flyway necesita `spring-boot-starter-flyway`, no solo `flyway-core`.

## D-004 · Reglas del proyecto sobre las reglas globales de gentle-ai/ODD
- **Contexto:** el agente trae instrucciones globales (gentle-ai/ODD) que chocan con el flujo
  AI-DLC de este proyecto: documento vivo, clasificación, compuertas, ramas, idioma, revisión y commits.
- **Decisión:** ante un choque, manda este proyecto:

  | Tema | gentle-ai/ODD global | Este proyecto (aplica) |
  |------|----------------------|------------------------|
  | Documento vivo | `odd/tasks/<feature>.md` con `S#` y `## Log` | `intenciones/I-XX-<nombre>/U-XX-<nombre>/unidad.md` desde `plantillas/unidad.md` (una carpeta por unidad); requisitos citados, no reescritos |
  | Clasificación | pequeña / grande | pequeña / sustancial / crítica |
  | Continuidad | lo autorizado sigue sin pedir permiso | cada compuerta la cierra un humano; el agente se detiene al final de cada bolt |
  | Ramas | una rama de feature con slices de ~400 líneas | una rama por bolt: `U-XX/B-XX-<nombre>` |
  | Idioma de artefactos | inglés por defecto | nombres de negocio y docs en español; términos técnicos en inglés |
  | Revisión nativa | `gentle-ai review` corre solo según riesgo y su "approved" cierra la revisión | al terminar cada bolt se corre `gentle-ai review`, se corrige lo que marque y su resultado se anota como evidencia en `unidad.md` junto al `./mvnw verify`; recién entonces el bolt pasa a "en revisión". Su "approved" es **requisito para pedir revisión humana, no la reemplaza**: el bolt solo queda aprobado con el ✔ del revisor humano |
  | Commits | Conventional Commits | Conventional Commits con alcance de bolt: `feat(U-XX/B-XX): descripción` para bolts y `chore: descripción` para tareas pequeñas; sin `Co-Authored-By` |

  Sin conflicto (se aplican igual): TDD, `./mvnw verify` antes de cerrar, detenerse si el código diverge de la spec.
  El espejo en Engram de `unidad.md` es solo respaldo; si difiere, manda el archivo.
- **Consecuencia:** el agente no crea `odd/tasks/`, no encadena bolts sin aprobación humana, no
  pide revisión humana sin el "approved" de `gentle-ai review` y no trata ese "approved" como
  aprobación del bolt.
