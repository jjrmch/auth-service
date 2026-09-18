# Auth Service

Microservicio de autenticación de la plataforma de gestión de biblioteca. Es el dueño de los usuarios y las credenciales (email + contraseña) y emite tokens JWT que el resto del ecosistema usa para autorizar peticiones.

## Qué hace

- Registro de usuarios con email, contraseña y nombre (los registros públicos se crean siempre con rol `CLIENTE`)
- Login con email y contraseña que devuelve un JWT firmado (HS256)
- Consulta del usuario autenticado (`/auth/me`) validando el token
- Contraseñas hasheadas con BCrypt (nunca se guardan en texto plano)
- Usuario ADMIN inicial creado al arrancar, configurable por variables de entorno
- Validación de datos de entrada (`@Email`, `@Size`, `@NotBlank`)
- Swagger UI en `/swagger-ui.html` con botón Authorize para probar con el token

## Stack

- Java 17
- Spring Boot 4.1
- Spring Security (BCrypt + OAuth2 Resource Server)
- Nimbus JOSE JWT (HS256)
- Spring Cloud 2025.1.2 (Eureka client)
- Spring Data JPA
- PostgreSQL
- springdoc-openapi

## Cómo ejecutarlo

Necesitas PostgreSQL (con la base de datos `auth`) y el discovery-service (Eureka) levantados. Puedes levantar todo el stack con docker-compose desde `biblioteca-deploy`, o ejecutar este servicio solo:

```bash
./mvnw spring-boot:run
```

La configuración se hace por variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_URL` | JDBC URL de PostgreSQL (default `jdbc:postgresql://localhost:5432/auth`) |
| `DB_USER` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |
| `EUREKA_URL` | URL del servidor Eureka (default `http://localhost:8761/eureka/`) |
| `JWT_SECRET` | Secreto compartido para firmar y validar los JWT (mínimo 32 caracteres). **Obligatorio** |
| `JWT_EXPIRATION_MINUTES` | Minutos de validez del token (default 60) |
| `ADMIN_EMAIL` | Email del ADMIN que se crea al arrancar (default `admin@biblioteca.com`) |
| `ADMIN_PASSWORD` | Contraseña del ADMIN inicial (default `admin1234`; cámbiala fuera de desarrollo) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/register` | Registra un usuario nuevo con rol CLIENTE (409 si el email ya existe) |
| POST | `/auth/login` | Valida las credenciales y devuelve el JWT (401 si son incorrectas) |
| GET | `/auth/me` | Datos del usuario del token (401 sin token o con token inválido) |

## Cómo se usa el token

```bash
# 1. Login
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@biblioteca.com","password":"admin1234"}'

# 2. Usar el token devuelto
curl http://localhost:8084/auth/me -H "Authorization: Bearer <token>"
```

El token es un JWT firmado con HS256 que incluye `sub` (email), `rol` y `nombre`. Cualquier servicio que comparta el mismo `JWT_SECRET` puede validarlo sin consultar a auth-service.

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

- No hay refresh tokens: cuando el JWT expira hay que volver a hacer login.
- No hay gestión de usuarios por parte del ADMIN (crear bibliotecarios, desactivar cuentas, etc.).
- No hay tests de negocio todavía, solo el test de contexto de Spring.

## Licencia

MIT
