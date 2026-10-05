# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y este proyecto sigue [Semantic Versioning](https://semver.org/lang/es/).

## [1.0.0] - 2026-10-05

### Añadido

- Registro de usuarios con rol `CLIENTE` y login con email/contraseña que devuelve un JWT firmado (HS256)
- Endpoint `/auth/me` para consultar el usuario autenticado
- Contraseñas hasheadas con BCrypt (nunca en texto plano)
- Usuario ADMIN inicial creado al arrancar, configurable con `ADMIN_EMAIL` / `ADMIN_PASSWORD`
- Validación de entrada (`@Email`, `@Size`, `@NotBlank`) y respuestas de error en JSON
- Documentación OpenAPI/Swagger con botón Authorize
- Registro en Eureka y configuración por variables de entorno
