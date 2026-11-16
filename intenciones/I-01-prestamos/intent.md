# I-01 · Prestar libros a socios

**Estado:** Aprobado
**Apoya a:** M1, M2 (encontradas en la vision del proyecto)

## Intención
Que la biblioteca pueda registrar sus libros, sus socios y prestar ejemplares controlando
vencimientos y multas.

## Criterio de éxito
Un socio activo puede llevarse un ejemplar disponible; al devolverlo tarde, el sistema calcula
su multa correctamente.

## Capacidades
- Registrar libros y sus ejemplares (dominio Catálogo)
- Registrar socios y su estado (dominio Socios)
- Prestar, devolver y cobrar multas (dominio Préstamos)

## Unidades en orden
| Id | Unidad | Dominio | Tamaño esperado | ¿Spec SDD? |
|----|--------|---------|-----------------|------------|
| U-01 | Catálogo de libros y ejemplares | Catálogo | Sustancial | No → solo `unidad.md` |
| U-02 | Registro de socios | Socios | Sustancial | No |
| U-03 | Préstamos, devoluciones y multas | Préstamos | Crítico (dinero) | Sí → requirements + design |

Tareas sueltas como "agregar endpoint de health" o "configurar Flyway" son **pequeñas**: van
directo a PR sin documento.

## Preguntas abiertas para el dueño de producto
- ¿Cuántos préstamos simultáneos puede tener un socio?
- Respuesta: varios
- ¿Monto de la multa por día? ¿Tiene tope?
- Respuesta: 0,75 usd  y no tiene tope
- ¿Un socio con multa pendiente puede pedir otro préstamo?
- no puede pedir
