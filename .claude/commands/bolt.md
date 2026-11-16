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
4. Corre `gentle-ai review` sobre el bolt y corrige lo que marque (vuelve a correr
   `./mvnw verify` tras cada corrección). Sin su "approved" no sigas: informa qué bloquea.
5. Actualiza el bolt en `unidad.md`: tareas marcadas, evidencia (`./mvnw verify` + resultado,
   `gentle-ai review` + resultado, pruebas nuevas), estado "en revisión", y "Avance y siguiente paso".
6. Resume qué cambió y qué debe mirar el revisor.

No marques el bolt como "aprobado": eso lo hace el revisor humano. El "approved" de
`gentle-ai review` es requisito para pedir revisión humana, no la reemplaza (D-004).
