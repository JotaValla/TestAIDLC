# Glosario

| Término | Significado | Dominio |
|---------|-------------|---------|
| Libro | Título del catálogo (ISBN, título, autor) | Catálogo |
| Ejemplar | Copia física de un libro; es lo que se presta | Catálogo |
| Socio | Persona registrada que puede pedir préstamos | Socios |
| Préstamo | Un ejemplar entregado a un socio con fecha de vencimiento | Préstamos |
| Vencimiento | Fecha límite de devolución (préstamo + 14 días) | Préstamos |
| Multa | Cargo por día de retraso en la devolución | Préstamos |

## Dominios (bounded contexts)
- **Catálogo**: libros y ejemplares.
- **Socios**: alta, baja, estado (activo/suspendido).
- **Préstamos**: prestar, devolver, multas. Consulta Catálogo y Socios, no los modifica.
