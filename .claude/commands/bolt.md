---
description: Construye un bolt con TDD y lo deja listo para revisión
argument-hint: <ruta de unidad.md> <id de bolt, ej. B-01>
---
Lee `CLAUDE.md`, `contexto/convenciones.md`, la unidad en $ARGUMENTS y, si existen,
`requirements.md` y `design.md` de la misma carpeta.

1. Crea la rama `U-XX/B-XX-<nombre>`.
2. Por cada criterio del bolt: escribe la prueba, ejecútala y muestra que falla; luego
   implementa lo mínimo para que pase.
3. Ejecuta `./mvnw verify`.
4. Actualiza el bolt en `unidad.md`: tareas marcadas, evidencia (comando + resultado + pruebas
   nuevas), estado "en revisión", y "Avance y siguiente paso".
5. Resume qué cambió y qué debe mirar el revisor.

No marques el bolt como "aprobado": eso lo hace el revisor humano.
