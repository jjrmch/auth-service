# Auth Service

Microservicio de autenticación de la plataforma de gestión de biblioteca. Es el dueño de los usuarios y las credenciales (email + contraseña) y expondrá la emisión de tokens JWT que el resto del ecosistema usará para autorizar peticiones.

En esta primera fase solo está el registro de usuarios; el login y la emisión de JWT llegan en la siguiente.

## Qué hace

- Registro de usuarios con email, contraseña y nombre
- Contraseñas hasheadas con BCrypt (nunca se guardan en texto plano)
- Los registros públicos se crean siempre con rol `CLIENTE`
- Validación de datos de entrada (`@Email`, `@Size`, `@NotBlank`)
- Swagger UI en `/swagger-ui.html`

## Stack

- Java 17
- Spring Boot 4.1
- Spring Security (BCrypt)
- Spring Cloud 2025.1.2 (Eureka client)
- Spring Data JPA
- PostgreSQL
- springdoc-openapi

## Cómo ejecutarlo

Necesitas PostgreSQL (con la base de datos `auth`) y el discovery-service (Eureka) levantados. Puedes levantar todo el stack con docker-compose desde `biblioteca-deploy`, o ejecutar este servicio solo:

```bash
./mvnw spring-boot:run
```

La configuración de la base de datos se hace por variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_URL` | JDBC URL de PostgreSQL (default `jdbc:postgresql://localhost:5432/auth`) |
| `DB_USER` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |
| `EUREKA_URL` | URL del servidor Eureka (default `http://localhost:8761/eureka/`) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/register` | Registra un usuario nuevo con rol CLIENTE (409 si el email ya existe) |

## Roles

| Rol | Uso previsto |
|---|---|
| `ADMIN` | Acceso total al sistema |
| `BIBLIOTECARIO` | Gestión de catálogo, ventas, alquileres y multas |
| `CLIENTE` | Consulta de catálogo y sus propias operaciones |

## Parte de un sistema más grande

La plataforma completa se compone de:

- [discovery-service](https://github.com/jjrmch/discovery-service) — servidor Eureka
- [gateway-service](https://github.com/jjrmch/gateway-service) — API Gateway (punto de entrada, `localhost:8080`)
- [catalog-service](https://github.com/jjrmch/catalog-service) — catálogo de libros y stock
- [transactions-service](https://github.com/jjrmch/transactions-service) — ventas, alquileres, reservas y multas
- [customer-service](https://github.com/jjrmch/customer-service) — datos de clientes
- [biblioteca-frontend](https://github.com/jjrmch/biblioteca-frontend) — panel web en React
- [biblioteca-deploy](https://github.com/jjrmch/biblioteca-deploy) — docker-compose con el stack completo

## Por mejorar

- Falta el login y la emisión/validación de JWT (siguiente fase).
- No hay gestión de usuarios por parte del ADMIN (crear bibliotecarios, etc.).

## Licencia

MIT
