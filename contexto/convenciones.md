# Convenciones

## Paquetes: uno por dominio, capas adentro
```
com.jotavalla.testworkflow
  catalogo/   api/  dominio/  infraestructura/
  socios/     api/  dominio/  infraestructura/
  prestamos/  api/  dominio/  infraestructura/
  compartido/       (solo utilidades sin lógica de negocio)
```
- `api`: controladores REST y DTOs (records).
- `dominio`: entidades, reglas y servicios. Sin anotaciones web.
- `infraestructura`: repositorios JPA.
- Un dominio no importa clases `dominio` de otro; se comunica por un servicio público.

## Código
- Nombres de negocio en español (igual que el glosario); términos técnicos en inglés si es lo habitual.
- DTOs como `record`. Validación con `@Valid` en la entrada.
- Errores de negocio: excepción propia por dominio → `@RestControllerAdvice` → `ProblemDetail` (RFC 7807).
- Dinero: `BigDecimal`, nunca `double`.

## Pruebas
- Nombre: `debe<Resultado>Cuando<Condicion>()`.
- Reglas de dominio: prueba unitaria. Endpoints: prueba con MockMvc.
- Cada criterio de un bolt (o `FR-xxx`) tiene al menos una prueba.

## Git
- Una rama por bolt: `U-01/B-01-alta-libro`.
- Mensaje: `[U-01/B-01] descripción`.
