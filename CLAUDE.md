# Biblioteca (proyecto de prueba del flujo AI-DLC + SDD + ODD)

Lee esto en cada sesión. Es un índice: el detalle vive en `contexto/`.

## Contexto permanente
- Visión y metas: `contexto/vision.md`
- Glosario del dominio: `contexto/glosario.md`
- Stack y comandos: `contexto/stack.md`
- Convenciones de código: `contexto/convenciones.md`
- Decisiones técnicas (ADR cortos): `contexto/decisiones.md`

## Cómo trabajamos
- Todo trabajo nace de una intención en `intenciones/I-XX-<nombre>/intent.md`.
- Clasifica cada tarea antes de empezar: **pequeña** (solo PR), **sustancial** (`unidad.md`),
  **crítica** (además `requirements.md` + `design.md`, solo si los humanos lo aprueban).
- Construye en bolts con TDD: primero la prueba que falla, luego el código.
- Cierra cada bolt con evidencia en `unidad.md` (comando ejecutado + resultado).
- Plantillas en `plantillas/`.

## Reglas duras
- Nunca te apruebes a ti mismo: cada compuerta la cierra un humano.
- Una unidad nunca cruza dos dominios.
- No reescribas requisitos ni diseño desde `unidad.md`: cítalos.
- Si el código diverge de la spec, detente y pregunta.
- `./mvnw verify` debe pasar antes de proponer cerrar un bolt.
- Si una regla global de gentle-ai/ODD choca con este proyecto, aplica D-004.
