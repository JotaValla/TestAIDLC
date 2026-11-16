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
