# Stack

- Java 27, Spring Boot 4.1.2, Maven (wrapper `./mvnw`) — ver D-003
- Spring Web MVC, Spring Data JPA, Bean Validation, Lombok
- Base de datos: PostgreSQL (la misma que usará ICAF). Migraciones con Flyway
  (`spring-boot-starter-flyway` + `flyway-database-postgresql`), en `src/main/resources/db/migration/`.
- Pruebas: JUnit 5, AssertJ, Mockito, `@SpringBootTest` + MockMvc
- Pruebas de integración contra Postgres real con Testcontainers (requiere Docker):
  `spring-boot-testcontainers`, `testcontainers-junit-jupiter`, `testcontainers-postgresql`.
  Las pruebas que levantan contexto importan `TestcontainersConfiguration` (`@ServiceConnection`).

Las versiones de dependencias las gestiona el parent de Spring Boot: no fijes `<version>` a mano.

## Configuración local
`src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/testworkflow
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
```
`validate` = Hibernate solo comprueba que las tablas coincidan con las entidades; quien crea
y cambia tablas es Flyway.

## Comandos
| Para | Comando |
|------|---------|
| Compilar + todas las pruebas | `./mvnw verify` |
| Una prueba | `./mvnw test -Dtest=NombreTest` |
| Levantar la app | `./mvnw spring-boot:run` |
